package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.core.ducks.network.ISPacketSpawnObject;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.WeaknessSwitch;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;

public class HelperInstantAttack implements Globals
{
    public static void attack(AutoCrystal module,
                              SPacketSpawnObject packet,
                              PacketEvent.Receive<?> event,
                              EntityEnderCrystal entityIn,
                              boolean slow)
    {
        attack(module, packet, event, entityIn, slow, true);
    }

    public static void attack(AutoCrystal module,
                              SPacketSpawnObject packet,
                              PacketEvent.Receive<?> event,
                              EntityEnderCrystal entityIn,
                              boolean slow,
                              boolean allowAntiWeakness)
    {
        ((ISPacketSpawnObject) event.getPacket()).setAttacked(true);
        CPacketUseEntity p = new CPacketUseEntity(entityIn);
        WeaknessSwitch w;
        if (allowAntiWeakness)
        {
            w = HelperRotation.antiWeakness(module);
            if (w.needsSwitch())
            {
                if (w.getSlot() == -1 || !module.instantAntiWeak.getValue())
                {
                    return;
                }
            }
        }
        else
        {
            w = WeaknessSwitch.NONE;
        }

        int lastSlot = mc.player.inventory.currentItem;
        Runnable runnable = () ->
        {
            if (w.getSlot() != -1)
            {
                module.antiWeaknessBypass.getValue().switchTo(w.getSlot());
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
                module.antiWeaknessBypass.getValue().switchBack(
                    lastSlot, w.getSlot());
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

        module.breakTimer.reset(slow ? module.slowBreakDelay.getValue()
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

}
