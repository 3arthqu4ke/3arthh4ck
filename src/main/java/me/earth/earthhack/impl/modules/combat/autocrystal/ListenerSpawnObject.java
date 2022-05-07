package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.WeaknessSwitch;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.List;

final class ListenerSpawnObject extends
        ModuleListener<AutoCrystal, PacketEvent.Receive<SPacketSpawnObject>>
{
    private static final ModuleCache<LegSwitch> LEG_SWITCH =
            Caches.getModule(LegSwitch.class);
    private static final ModuleCache<AntiSurround> ANTISURROUND =
            Caches.getModule(AntiSurround.class);
    private static final SettingCache<Float, NumberSetting<Float>, Safety> DMG =
            Caches.getSetting(Safety.class, Setting.class, "MaxDamage", 4.0f);

    public ListenerSpawnObject(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        try
        {
            onEvent(event);
        }
        catch (Throwable t) // ConcurrentModification in our ArmorList
        {
            t.printStackTrace();
        }
    }

    private void onEvent(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        if (mc.player == null || mc.world == null || !module.spectator.getValue() && mc.player.isSpectator())
        {
            return;
        }

        SPacketSpawnObject packet = event.getPacket();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        EntityEnderCrystal entity = new EntityEnderCrystal(mc.world, x, y, z);

        if (module.simulatePlace.getValue() != 0)
        {
            event.addPostEvent(() ->
            {
                if (mc.world == null)
                {
                    return;
                }

                Entity e = mc.world.getEntityByID(packet.getEntityID());
                if (e instanceof EntityEnderCrystal)
                {
                    module.crystalRender.onSpawn((EntityEnderCrystal) e);
                }
            });
        }

        if (packet.getType() != 51
            || !module.instant.getValue()
            || module.isPingBypass()
            || !module.breakTimer.passed(module.breakDelay.getValue())
            || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false)
            || LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false))
        {
            return;
        }

        BlockPos pos = new BlockPos(x, y, z);
        CrystalTimeStamp stamp = module.placed.get(pos);
        entity.setShowBottom(false);
        entity.setEntityId(packet.getEntityID());
        entity.setUniqueId(packet.getUniqueId());

        if (!module.alwaysCalc.getValue()
            && stamp != null
            && stamp.isValid()
            && (stamp.getDamage() > module.slowBreakDamage.getValue()
                || stamp.isShield()
                || module.breakTimer.passed(module.slowBreakDelay.getValue())
                || pos.down().equals(module.antiTotemHelper.getTargetPos())))
        {
            float damage = checkPos(entity);
            if (damage <= -1000.0f)
            {
                MutableWrapper<Boolean> a = new MutableWrapper<>(false);
                module.rotation = module.rotationHelper.forBreaking(entity, a);
                // set it once more once we got the real entity
                event.addPostEvent(() ->
                {
                    if (mc.world != null)
                    {
                        Entity e = mc.world.getEntityByID(packet.getEntityID());
                        if (e != null)
                        {
                            module.post.add(
                                    module.rotationHelper.post(e, a));
                            module.rotation =
                                    module.rotationHelper.forBreaking(e, a);

                            module.setCrystal(e);
                        }
                    }
                });

                return;
            }

            if (damage < 0.0f)
            {
                return;
            }

            if (damage > module.shieldSelfDamage.getValue() && stamp.isShield())
            {
                return;
            }

            attack(packet,
                    event,
                    entity,
                    stamp.getDamage() <= module.slowBreakDamage.getValue());
        }
        else if (module.asyncCalc.getValue() || module.alwaysCalc.getValue())
        {
            List<EntityPlayer> players = Managers.ENTITIES.getPlayers();
            if (players == null)
            {
                return;
            }

            float self = checkPos(entity);
            if (self < 0.0f)
            {
                // TODO: ROTATIONS HERE?
                return;
            }

            boolean slow = true;
            boolean attack = false;
            for (EntityPlayer player : players)
            {
                if (player == null
                    || EntityUtil.isDead(player)
                    || player.getDistanceSq(x, y, z) > 144)
                {
                    continue;
                }

                if (Managers.FRIENDS.contains(player))
                {
                    if (module.antiFriendPop.getValue()
                                            .shouldCalc(AntiFriendPop.Break))
                    {
                        if (module.damageHelper
                                  .getDamage(x, y, z, player)
                                > EntityUtil.getHealth(player) - 0.5f)
                        {
                            attack = false;
                            break;
                        }
                    }

                    continue;
                }

                float dmg = module.damageHelper.getDamage(x, y, z, player);
                if ((dmg > self
                        || module.suicide.getValue()
                            && dmg >= module.minDamage.getValue())
                    && (dmg > module.slowBreakDamage.getValue()
                        || module.shouldDanger()
                        || module.breakTimer.passed(module.slowBreakDelay
                                                          .getValue())))
                {
                    slow = slow && dmg <= module.slowBreakDamage.getValue();
                    attack = true;
                }
            }

            if (attack)
            {
                attack(packet, event, entity,
                       (stamp == null || !stamp.isShield()) && slow);
            }
            else if (stamp != null
                && stamp.isShield()
                && self >= 0.0f
                && self <= module.shieldSelfDamage.getValue())
            {
                attack(packet, event, entity, false);
            }
        }

        if (module.spawnThread.getValue())
        {
            module.threadHelper.schedulePacket(event);
        }
    }

    private void attack(SPacketSpawnObject packet,
                        PacketEvent.Receive<?> event,
                        EntityEnderCrystal entityIn,
                        boolean slow)
    {
        CPacketUseEntity p = new CPacketUseEntity(entityIn);
        WeaknessSwitch w = HelperRotation.antiWeakness(module);
        if (w.needsSwitch())
        {
            if (w.getSlot() == -1 || !module.instantAntiWeak.getValue())
            {
                return;
            }
        }

        int lastSlot = mc.player.inventory.currentItem;
        Runnable runnable = () ->
        {
            if (w.getSlot() != -1)
            {
                switch (module.antiWeaknessBypass.getValue())
                {
                    case None:
                        InventoryUtil.switchTo(w.getSlot());
                        break;
                    case Slot:
                        InventoryUtil.switchToBypassAlt(
                                InventoryUtil.hotbarToInventory(w.getSlot()));
                        break;
                    case Pick:
                        InventoryUtil.bypassSwitch(w.getSlot());
                        break;
                }
            }

            if (module.breakSwing.getValue() == SwingTime.Pre)
            {
                Swing.Packet.swing(EnumHand.MAIN_HAND);
            }

            mc.player.connection.sendPacket(p);

            if (module.breakSwing.getValue() == SwingTime.Post)
            {
                Swing.Packet.swing(EnumHand.MAIN_HAND);
            }

            if (w.getSlot() != -1)
            {
                switch (module.antiWeaknessBypass.getValue())
                {
                    case None:
                        InventoryUtil.switchTo(lastSlot);
                        break;
                    case Slot:
                        InventoryUtil.switchToBypassAlt(
                                InventoryUtil.hotbarToInventory(w.getSlot()));
                        break;
                    case Pick:
                        InventoryUtil.bypassSwitch(w.getSlot());
                        break;
                }
            }
        };

        if (w.getSlot() != -1)
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, runnable);
        }
        else
        {
            runnable.run();
        }

        module.breakTimer.reset(slow
                ? module.slowBreakDelay.getValue()
                : module.breakDelay.getValue());

        event.addPostEvent(() ->
        {
            Entity entity = mc.world.getEntityByID(packet.getEntityID());
            if (entity instanceof EntityEnderCrystal)
            {
                module.setCrystal(entity);
            }
        });

        if (module.simulateExplosion.getValue())
        {
            HelperUtil.simulateExplosion(
                module, packet.getX(), packet.getY(), packet.getZ());
        }

        if (module.pseudoSetDead.getValue())
        {
            event.addPostEvent(() ->
            {
                Entity entity = mc.world.getEntityByID(packet.getEntityID());
                if (entity != null)
                {
                    ((IEntity) entity).setPseudoDead(true);
                }
            });

            return;
        }

        if (module.instantSetDead.getValue())
        {
            event.setCancelled(true);
            mc.addScheduledTask(() ->
            {
                Entity entity = mc.world.getEntityByID(packet.getEntityID());
                if (entity instanceof EntityEnderCrystal)
                {
                    module.crystalRender.onSpawn((EntityEnderCrystal) entity);
                }

                if (!event.isCancelled())
                {
                    return;
                }

                EntityTracker.updateServerPosition(entityIn,
                                                   packet.getX(),
                                                   packet.getY(),
                                                   packet.getZ());
                Managers.SET_DEAD.setDead(entityIn);
            });
        }
    }

    private float checkPos(Entity entity)
    {
        BreakValidity validity = HelperUtil.isValid(module, entity);
        switch (validity)
        {
            case INVALID:
                return -1.0f;
            case ROTATIONS:
                float damage = getSelfDamage(entity);
                if (damage < 0)
                {
                    return damage;
                }

                return -1000.0f - damage;
            case VALID:
            default:
        }

        return getSelfDamage(entity);
    }

    private float getSelfDamage(Entity entity)
    {
        float damage = module.damageHelper.getDamage(entity);
        if (damage > EntityUtil.getHealth(mc.player) - 1.0f
                || damage > DMG.getValue())
        {
            Managers.SAFETY.setSafe(false);
        }

        return damage > module.maxSelfBreak.getValue()
                || damage > EntityUtil.getHealth(mc.player) - 1.0f
                && !module.suicide.getValue()
                    ? -1.0f
                    : damage;
    }

}
