package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.anvilaura.AnvilAura;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.ArmUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
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
import net.minecraft.util.math.Vec3i;

//TODO: maybe account for Tps?
final class ListenerUpdate extends ModuleListener<Speedmine, UpdateEvent>
{
    private static final Vec3i[] CRYSTAL_OFFSETS = new Vec3i[]
            {
                    new Vec3i(1, -1, 0),
                    new Vec3i(0, -1, 1),
                    new Vec3i(-1, -1, 0),
                    new Vec3i(0, -1, -1),
                    new Vec3i(0, 0, 0) // check this one last!
            };

    private static final ModuleCache<Nuker> NUKER =
            Caches.getModule(Nuker.class);
    private static final ModuleCache<AutoCrystal> AUTOCRYSTAL =
            Caches.getModule(AutoCrystal.class);
    private static final ModuleCache<AnvilAura> ANVIL_AURA =
            Caches.getModule(AnvilAura.class);
    private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE =
            Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);

    private final IBlockStateHelper helper = new BlockStateHelper();

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
        module.checkReset();
        if (PlayerUtil.isCreative(mc.player)
                || NUKER.isEnabled() && NUKE.getValue()
                || ANVIL_AURA.isEnabled() && ANVIL_AURA.get().isMining())
        {
            return;
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
                        || module.mode.getValue() == MineMode.Instant
                        || module.mode.getValue() == MineMode.Civ)
                    && mc.player.getDistanceSq(module.pos) >
                            MathUtil.square(module.range.getValue()))
            {
                module.abortCurrentPos();
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

            module.maxDamage = 0.0f;
            for (int i = 0; i < 9; i++)
            {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                float damage = MineUtil.getDamage(stack, module.pos, module.onGround.getValue());
                module.damages[i] = MathUtil.clamp(module.damages[i] + damage, 0.0f, Float.MAX_VALUE);

                if (module.damages[i] > module.maxDamage)
                {
                    module.maxDamage = module.damages[i];
                }
            }

            if (module.normal.getValue())
            {
                int fastSlot = -1;
                for (int i = 0; i < module.damages.length; i++)
                {
                    if (module.damages[i] >= module.limit.getValue())
                    {
                        fastSlot = i;
                        if (i == mc.player.inventory.currentItem)
                        {
                            break;
                        }
                    }
                }

                if ((module.damages[mc.player.inventory.currentItem] >= module.limit.getValue()
                        || module.swap.getValue() && fastSlot != -1)
                        && (!module.checkPacket.getValue() || !module.sentPacket))
                {
                    int crystalSlot;
                    BlockPos crystalPos;
                    boolean swap = module.swap.getValue();
                    int lastSlot = mc.player.inventory.currentItem;

                    if (module.placeCrystal.getValue()
                            && (crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL)) != -1
                            && (crystalPos = calcCrystal(module.pos)) != null)
                    {
                        RayTraceResult ray = RotationUtil.rayTraceTo(crystalPos, mc.world);
                        if (ray != null && ray.sideHit != null && ray.hitVec != null)
                        {
                            placeCrystal(crystalPos, crystalSlot, ray);
                            if (!swap || module.rotate.getValue()
                                    && module.limitRotations.getValue()
                                    && !RotationUtil.isLegit(module.pos, module.facing))
                            {
                                InventoryUtil.switchTo(lastSlot);
                            }
                        }
                    }

                    if (swap)
                    {
                        InventoryUtil.switchTo(fastSlot);
                    }

                    boolean toAir = module.toAir.getValue();
                    InventoryUtil.syncItem();
                    if (module.sendStopDestroy(module.pos, module.facing, toAir))
                    {
                        module.postSend(toAir);
                    }

                    if (swap)
                    {
                        InventoryUtil.switchTo(lastSlot);
                    }
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
                            mc.player.connection.sendPacket(animation);
                        } else {
                            final int crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
                            if (crystalSlot != -1) {
                                InventoryUtil.switchTo(crystalSlot);
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
                                InventoryUtil.switchTo(lastSlot);
                            }
                        }
                    }
                }
                if (module.swap.getValue())
                {
                    InventoryUtil.switchTo(pickSlot);
                }
                NetworkUtil.sendPacketNoEvent(new CPacketPlayerDigging(
                                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                                module.pos,
                                module.facing),
                        false);
                if (module.swap.getValue())
                {
                    InventoryUtil.switchTo(lastSlot);
                }

                if (module.toAir.getValue())
                {
                    mc.playerController.onPlayerDestroyBlock(module.pos);
                }

                module.onSendPacket();
            }
        }
    }

    private BlockPos calcCrystal(BlockPos mined)
    {
        helper.clearAllStates();
        helper.addAir(mined);
        BlockPos bestPos = null;
        float bestDamage = Float.MIN_VALUE;
        for (Vec3i offset : CRYSTAL_OFFSETS)
        {
            BlockPos pos = mined.add(offset);
            if (BlockUtil.isCrystalPosInRange(pos, module.crystalRange.getValue(), module.crystalTrace.getValue(),
                    module.crystalBreakTrace.getValue())
                    && BlockUtil.canPlaceCrystal(pos, false, module.newVer.getValue(), mc.world.loadedEntityList,
                    module.newVerEntities.getValue(), 0L))
            {
                float selfDamage = DamageUtil.calculate(pos, mc.player, helper);
                if (selfDamage > module.maxSelfDmg.getValue())
                {
                    continue;
                }

                for (EntityPlayer player : mc.world.playerEntities)
                {
                    if (player != null
                            && !player.equals(mc.player)
                            && !player.equals(RotationUtil.getRotationPlayer())
                            && !Managers.FRIENDS.contains(player)
                            && !EntityUtil.isDead(player)
                            && player.getDistanceSq(pos) < 144)
                    {
                        float damage = DamageUtil.calculate(pos, player, helper);
                        if (damage > module.minDmg.getValue() && damage > bestDamage)
                        {
                            bestPos = pos;
                            bestDamage = damage;
                        }
                    }
                }
            }
        }

        return bestPos;
    }

    private void placeCrystal(BlockPos pos, int slot, RayTraceResult ray)
    {
        EnumHand hand = InventoryUtil.getHand(slot);
        float[] f = RayTraceUtil.hitVecToPlaceVec(pos, ray.hitVec);
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
        {
            if (slot != -2)
            {
                InventoryUtil.switchTo(slot);
            }

            if (AUTOCRYSTAL.get().placeSwing.getValue() == SwingTime.Pre)
            {
                AUTOCRYSTAL.get().rotationHelper.swing(hand, false);
            }

            mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItemOnBlock(
                            pos, ray.sideHit, hand, f[0], f[1], f[2]));
        });

        if (AUTOCRYSTAL.get().placeSwing.getValue() == SwingTime.Post)
        {
            AUTOCRYSTAL.get().rotationHelper.swing(hand, false);
        }

        AUTOCRYSTAL.get().placed.put(pos, new CrystalTimeStamp(Float.MAX_VALUE, false));
    }

}
