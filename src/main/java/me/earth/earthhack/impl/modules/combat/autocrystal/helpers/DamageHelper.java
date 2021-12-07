package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class DamageHelper implements Globals
{
    private final Setting<Boolean> terrainCalc;
    private final Setting<Integer> bExtrapolation;
    private final Setting<Integer> pExtrapolation;
    private final Setting<Boolean> selfExtrapolation;
    private final Setting<Boolean> obbyTerrain;
    private final PositionHelper positionHelper;

    public DamageHelper(PositionHelper positionHelper,
                        Setting<Boolean> terrainCalc,
                        Setting<Integer> extrapolation,
                        Setting<Integer> bExtrapolation,
                        Setting<Boolean> selfExtrapolation,
                        Setting<Boolean> obbyTerrain)
    {
        this.positionHelper = positionHelper;
        this.terrainCalc       = terrainCalc;
        this.pExtrapolation    = extrapolation;
        this.bExtrapolation    = bExtrapolation;
        this.selfExtrapolation = selfExtrapolation;
        this.obbyTerrain       = obbyTerrain;
    }

    public float getDamage(Entity crystal)
    {
        return getDamage(crystal.posX,
                         crystal.posY,
                         crystal.posZ,
                         Managers.POSITION.getBB(),
                         mc.player);
    }

    public float getDamage(Entity crystal, AxisAlignedBB bb)
    {
        return DamageUtil.calculate(crystal.posX,
                                    crystal.posY,
                                    crystal.posZ,
                                    bb,
                                    mc.player);
    }

    public float getDamage(Entity crystal, EntityLivingBase base)
    {
        if (bExtrapolation.getValue() != 0)
        {
            return getDamage(crystal.posX,
                             crystal.posY,
                             crystal.posZ,
                             extrapolateEntity(base, bExtrapolation.getValue()),
                             base);
        }

        return getDamage(crystal.posX, crystal.posY, crystal.posZ, base);
    }

    public float getDamage(BlockPos pos)
    {
        return getDamage(pos, RotationUtil.getRotationPlayer());
    }

    public float getDamage(BlockPos pos, EntityLivingBase base)
    {
        if (pExtrapolation.getValue() != 0
                && (selfExtrapolation.getValue()
                    || !base.equals(RotationUtil.getRotationPlayer())))
        {
            return getDamage(pos.getX() + 0.5f,
                             pos.getY() + 1,
                             pos.getZ() + 0.5f,
                             extrapolateEntity(base, pExtrapolation.getValue()),
                             base);
        }

        return getDamage(pos.getX() + 0.5f,
                         pos.getY() + 1,
                         pos.getZ() + 0.5f,
                         base);
    }

    public float getDamage(double x,
                           double y,
                           double z,
                           EntityLivingBase base)
    {
        return getDamage(x, y, z, base.getEntityBoundingBox(), base);
    }

    public float getDamage(double x,
                           double y,
                           double z,
                           AxisAlignedBB bb,
                           EntityLivingBase base)
    {
        return DamageUtil.calculate(x, y, z, bb, base, terrainCalc.getValue());
    }

    public float getObbyDamage(BlockPos pos,
                               IBlockStateHelper world)
    {
        AxisAlignedBB bb;
        if (selfExtrapolation.getValue())
        {
            bb = extrapolateEntity(RotationUtil.getRotationPlayer(),
                                   pExtrapolation.getValue());
        }
        else
        {
            bb = RotationUtil.getRotationPlayer().getEntityBoundingBox();
        }

        return getObbyDamage(pos,
                             mc.player,
                             bb,
                             world);
    }

    public float getObbyDamage(BlockPos pos,
                               EntityLivingBase base,
                               IBlockStateHelper world)
    {
        return getObbyDamage(pos,
                             base,
                             extrapolateEntity(base, pExtrapolation.getValue()),
                             world);
    }

    public float getObbyDamage(BlockPos pos,
                               EntityLivingBase base,
                               AxisAlignedBB bb,
                               IBlockStateHelper world)
    {
        return DamageUtil.calculate(
                pos.getX() + 0.5f,
                pos.getY() + 1,
                pos.getZ() + 0.5f,
                bb,
                base,
                world,
                obbyTerrain.getValue());
    }

    public AxisAlignedBB extrapolateEntity(Entity entity, int ticks)
    {
        if (ticks == 0)
        {
            return entity.getEntityBoundingBox();
        }

        // TODO THIS! earf can you test dis for me i am on my laptop :3
        // no -_-
        MotionTracker tracker = positionHelper.getTrackerFromEntity(entity);
        if (tracker == null) return entity.getEntityBoundingBox();

        MotionTracker copy = new MotionTracker(mc.world, tracker);
        for (int i = 0; i < ticks; i++)
        {
            copy.updateSilent();
        }

        return copy.getEntityBoundingBox();
    }

}
