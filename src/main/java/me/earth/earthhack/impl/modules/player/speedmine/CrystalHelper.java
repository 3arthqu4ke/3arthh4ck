package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;

public class CrystalHelper implements Globals {
    private static final Vec3i[] CRYSTAL_OFFSETS = new Vec3i[]
        {
            new Vec3i(1, -1, 0),
            new Vec3i(0, -1, 1),
            new Vec3i(-1, -1, 0),
            new Vec3i(0, -1, -1),
            new Vec3i(0, 0, 0) // check this one last!
        };
    private static final ModuleCache<AutoCrystal> AUTOCRYSTAL =
        Caches.getModule(AutoCrystal.class);
    private static final ModuleCache<Offhand> OFFHAND =
        Caches.getModule(Offhand.class);
    private final IBlockStateHelper helper = new BlockStateHelper();

    private final Speedmine module;

    public CrystalHelper(Speedmine module) {
        this.module = module;
    }

    public BlockPos calcCrystal(BlockPos mined)
    {
        return calcCrystal(mined, null, false);
    }

    public BlockPos calcCrystal(BlockPos mined, EntityPlayer player, boolean ignoreCrystals)
    {
        helper.clearAllStates();
        helper.addAir(mined);
        DamageResult result = new DamageResult();
        result.bestDamage = Float.MIN_VALUE;
        for (Vec3i offset : CRYSTAL_OFFSETS)
        {
            BlockPos pos = mined.add(offset);
            if (BlockUtil.isCrystalPosInRange(pos, module.crystalRange.getValue(), module.crystalTrace.getValue(),
                                              module.crystalBreakTrace.getValue())
                && BlockUtil.canPlaceCrystal(pos, ignoreCrystals, module.newVer.getValue(), mc.world.loadedEntityList,
                                             module.newVerEntities.getValue(), 0L))
            {
                float selfDamage = DamageUtil.calculate(pos, mc.player, helper);
                if (selfDamage > module.maxSelfDmg.getValue())
                {
                    continue;
                }

                if (player == null)
                {
                    for (EntityPlayer p : mc.world.playerEntities)
                    {
                        checkPlayer(p, pos, result);
                    }
                }
                else
                {
                    checkPlayer(player, pos, result);
                }
            }
        }

        return result.bestPos;
    }

    private void checkPlayer(EntityPlayer player,
                             BlockPos pos,
                             DamageResult result) {
        if (player != null
            && !player.equals(mc.player)
            && !player.equals(RotationUtil.getRotationPlayer())
            && !Managers.FRIENDS.contains(player)
            && !EntityUtil.isDead(player)
            && player.getDistanceSq(pos) < 144)
        {
            float damage = DamageUtil.calculate(pos, player, helper);
            if (damage > module.minDmg.getValue() && damage > result.bestDamage)
            {
                result.bestPos = pos;
                result.bestDamage = damage;
            }
        }
    }

    public void placeCrystal(BlockPos pos, int slot, RayTraceResult ray)
    {
        EnumHand hand = module.offhandPlace.getValue()
            ? EnumHand.OFF_HAND
            : InventoryUtil.getHand(slot);

        float[] f = RayTraceUtil.hitVecToPlaceVec(pos, ray.hitVec);
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
        {
            OffhandMode mode = null;
            if (slot != -2)
            {
                if (module.offhandPlace.getValue())
                {
                    mode = OFFHAND.get().getMode();
                    OFFHAND.get().forceMode(OffhandMode.CRYSTAL);
                    if (OFFHAND.get().getMode() != OffhandMode.CRYSTAL)
                    {
                        return;
                    }
                }
                else
                {
                    module.cooldownBypass.getValue().switchTo(slot);
                }
            }

            if (AUTOCRYSTAL.get().placeSwing.getValue() == SwingTime.Pre)
            {
                AUTOCRYSTAL.get().rotationHelper.swing(hand, false);
            }

            mc.player.connection.sendPacket(
                new CPacketPlayerTryUseItemOnBlock(
                    pos, ray.sideHit, hand, f[0], f[1], f[2]));

            if (AUTOCRYSTAL.get().placeSwing.getValue() == SwingTime.Post)
            {
                AUTOCRYSTAL.get().rotationHelper.swing(hand, false);
            }

            if (module.offhandSilent.getValue() && mode != null)
            {
                OFFHAND.get().setMode(mode);
                if (module.megaSilent.getValue())
                {
                    OFFHAND.get().forceMode(mode);
                }
            }
        });

        if (AUTOCRYSTAL.isPresent())
        {
            AUTOCRYSTAL.get().placed.put(
                pos.up(), new CrystalTimeStamp(Float.MAX_VALUE, false));
            AUTOCRYSTAL.get().bombPos = pos.up();
        }
    }

    public boolean doCrystalPlace(BlockPos crystalPos, int crystalSlot, int lastSlot, boolean swap)
    {
        if (module.antiAntiSilentSwitch.getValue()
            && !module.aASSSwitchTimer.passed(module.aASSwitchTime.getValue()))
        {
            return true;
        }

        RayTraceResult ray = RotationUtil.rayTraceTo(crystalPos, mc.world);
        if (ray != null && ray.sideHit != null && ray.hitVec != null)
        {
            module.crystalHelper.placeCrystal(crystalPos, crystalSlot, ray);
            boolean swappedBack = false;
            if (!swap || module.rotate.getValue()
                && module.limitRotations.getValue()
                && !RotationUtil.isLegit(module.pos, module.facing))
            {
                swappedBack = true;
                // TODO:?????????????????????????
                module.cooldownBypass.getValue().switchBack(
                    lastSlot, crystalSlot);
            }

            if (module.antiAntiSilentSwitch.getValue()) {
                if (!swappedBack && !module.offhandPlace.getValue()) {
                    module.cooldownBypass.getValue().switchBack(
                        lastSlot, crystalSlot);
                }

                module.aASSSwitchTimer.reset();
                return true;
            }
        }

        return false;
    }

    private static final class DamageResult
    {
        public BlockPos bestPos;
        public float bestDamage;
    }

}
