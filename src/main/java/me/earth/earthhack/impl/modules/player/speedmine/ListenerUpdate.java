package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.core.mixins.util.MixinOptions;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.anvilaura.AnvilAura;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.modules.player.speedmine.mode.ESPMode;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.ArmUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CRenderPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

//TODO: maybe account for Tps?
final class ListenerUpdate extends ModuleListener<Speedmine, UpdateEvent>
{
    private static final ModuleCache<Nuker> NUKER =
            Caches.getModule(Nuker.class);
    private static final ModuleCache<AutoCrystal> AUTOCRYSTAL =
            Caches.getModule(AutoCrystal.class);
    private static final ModuleCache<AnvilAura> ANVIL_AURA =
            Caches.getModule(AnvilAura.class);
    private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE =
            Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);

    public ListenerUpdate(Speedmine module)
    {
        super(module, UpdateEvent.class, -10);
    }

    private EntityPlayer getPlacePlayer(BlockPos pos)
    {
        for (EntityPlayer player : mc.world.playerEntities)
        {
            if (Managers.FRIENDS.contains(player) || player == mc.player) continue;
            final BlockPos playerPos = PositionUtil.getPosition(player);
            for (EnumFacing facing : EnumFacing.HORIZONTALS)
            {
                if (playerPos.offset(facing).equals(pos))
                {
                    return player;
                }
            }
            if (playerPos.offset(EnumFacing.UP).offset(EnumFacing.UP).equals(pos))
            {
                return player;
            }
        }

        return null;
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        if (PingBypass.isConnected() && !event.isPingBypass())
        {
            return;
        }

        module.checkReset();
        if (PlayerUtil.isCreative(mc.player)
                || NUKER.isEnabled() && NUKE.getValue()
                || ANVIL_AURA.isEnabled() && ANVIL_AURA.get().isMining())
        {
            return;
        }

        if (!PlayerUtil.isCreative(mc.player)
            && PingBypass.isConnected()
            && module.esp.getValue() != ESPMode.None
            && module.bb != null)
        {
            PingBypass.sendPacket(new S2CRenderPacket(
                module.bb, module.pbOutline.getValue(),
                module.pbColor.getValue()));
        }

        ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
        if (!module.multiTask.getValue()
                && (module.noReset.getValue()
                    || module.mode.getValue() == MineMode.Reset)
                && mc.gameSettings.keyBindUseItem.isKeyDown())
        {
            ((IPlayerControllerMP) mc.playerController)
                    .setIsHittingBlock(false);
        }

        if (module.pos != null)
        {
            if ((module.mode.getValue() == MineMode.Smart
                        || module.mode.getValue() == MineMode.Fast
                        || module.mode.getValue() == MineMode.Instant
                        || module.mode.getValue() == MineMode.Civ)
                    && mc.player.getDistanceSq(module.pos) >
                            MathUtil.square(module.range.getValue()))
            {
                module.abortCurrentPos();
                return;
            }

            if (module.mode.getValue() == MineMode.Fast) {
                module.fastHelper.onUpdate();
                return;
            }

            if (module.mode.getValue() == MineMode.Civ
                    && module.facing != null
                    && !BlockUtil.isAir(module.pos)
                    && !module.isPausing()
                    && module.delayTimer.passed(module.realDelay.getValue()))
            {
                ArmUtil.swingPacket(EnumHand.MAIN_HAND);
                module.sendStopDestroy(module.pos, module.facing, false);
            }

            module.updateDamages();
            if (module.normal.getValue())
            {
                int fastSlot = module.getFastSlot();
                boolean prePlace = false;
                if ((module.damages[mc.player.inventory.currentItem] >= module.limit.getValue()
                        || module.swap.getValue() && fastSlot != -1
                        || (prePlace = module.prePlaceCheck()))
                        && (!module.checkPacket.getValue() || !module.sentPacket))
                {
                    boolean finalPrePlace = prePlace;
                    Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                    {
                        int crystalSlot;
                        BlockPos crystalPos;
                        boolean swap = module.swap.getValue();
                        int lastSlot = mc.player.inventory.currentItem;

                        if (module.placeCrystal.getValue()
                                && (crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL)) != -1
                                && (crystalPos = module.crystalHelper.calcCrystal(module.pos)) != null
                                && module.crystalHelper.doCrystalPlace(crystalPos, crystalSlot, lastSlot, swap)
                                    || finalPrePlace)
                        {
                            return;
                        }

                        module.postCrystalPlace(fastSlot, lastSlot, swap);
                    });
                }

                return;
            }

            int pickSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
            if ((module.damages[mc.player.inventory.currentItem] >=
                            module.limit.getValue())
                    || (pickSlot >= 0 && module.damages[pickSlot] >= module.limit.getValue())
                    && !module.pausing
                    && module.breakBind.getValue().getKey() == -1)
            {
                int lastSlot = mc.player.inventory.currentItem;
                final EntityPlayer placeTarg = getPlacePlayer(module.pos);
                if (placeTarg != null) {
                    // System.out.println(placeTarg.getName());
                    final BlockPos p = PlayerUtil.getBestPlace(module.pos, placeTarg);
                    if (module.placeCrystal.getValue() && AUTOCRYSTAL.isEnabled() && p != null && BlockUtil.canPlaceCrystal(p,false,false)) {
                        final RayTraceResult result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, p);

                        if (mc.player.getHeldItemOffhand() != ItemStack.EMPTY && mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                            final CPacketPlayerTryUseItemOnBlock place =
                                    new CPacketPlayerTryUseItemOnBlock(p,
                                            result.sideHit,
                                            EnumHand.OFF_HAND,
                                            (float) result.hitVec.x,
                                            (float) result.hitVec.y,
                                            (float) result.hitVec.z);
                            final CPacketAnimation animation =
                                    new CPacketAnimation(EnumHand.OFF_HAND);
                            InventoryUtil.syncItem();
                            mc.player.connection.sendPacket(place);
                            if (AUTOCRYSTAL.isPresent()) {
                                AUTOCRYSTAL.get().placed.put(p.up(), new CrystalTimeStamp(Float.MAX_VALUE, false));
                                AUTOCRYSTAL.get().bombPos = p.up();
                            }
                            mc.player.connection.sendPacket(animation);
                        } else {
                            final int crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
                            if (crystalSlot != -1) {
                                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                                    module.cooldownBypass.getValue().switchTo(crystalSlot);
                                    final CPacketPlayerTryUseItemOnBlock place =
                                        new CPacketPlayerTryUseItemOnBlock(p,
                                                                           result.sideHit,
                                                                           EnumHand.MAIN_HAND,
                                                                           (float) result.hitVec.x,
                                                                           (float) result.hitVec.y,
                                                                           (float) result.hitVec.z);
                                    final CPacketAnimation animation =
                                        new CPacketAnimation(EnumHand.MAIN_HAND);
                                    mc.player.connection.sendPacket(place);
                                    mc.player.connection.sendPacket(animation);
                                    module.cooldownBypass.getValue().switchBack(lastSlot, crystalSlot);
                                });
                            }
                        }
                    }
                }

                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                {
                    if (module.swap.getValue())
                    {
                        module.cooldownBypass.getValue().switchTo(pickSlot);
                    }

                    NetworkUtil.sendPacketNoEvent(
                        new CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            module.pos,
                            module.facing),
                        false);

                    if (module.swap.getValue())
                    {
                        module.cooldownBypass.getValue().switchBack(
                            lastSlot, pickSlot);
                    }
                });

                if (module.toAir.getValue())
                {
                    mc.playerController.onPlayerDestroyBlock(module.pos);
                }

                module.onSendPacket();
            }
        }
    }

}
