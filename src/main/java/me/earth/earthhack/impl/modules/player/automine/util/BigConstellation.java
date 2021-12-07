package me.earth.earthhack.impl.modules.player.automine.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

public class BigConstellation implements IConstellation, Globals
{
    private final IBlockStateHelper helper;
    private final IAutomine automine;
    private final BlockPos[] positions;
    private final IBlockState[] states;
    private final EntityPlayer target;

    private int blockStateChanges;
    private boolean valid;

    public BigConstellation(IAutomine automine,
                            BlockPos[] positions,
                            IBlockState[] states,
                            EntityPlayer target)
    {
        this.automine  = automine;
        this.positions = positions;
        this.states    = states;
        this.target    = target;
        this.valid     = true;
        this.helper    = new BlockStateHelper();
        this.helper.addBlockState(positions[0],
                                  Blocks.OBSIDIAN.getDefaultState());
        for (int i = 1; i < positions.length; i++)
        {
            this.helper.addAir(positions[i]);
        }
    }

    @Override
    public void update(IAutomine automine)
    {
        valid = false;
        BlockPos attackPos = null;
        for (int i = 0; i < states.length; i++)
        {
            states[i] = mc.world.getBlockState(positions[i]);
            if (i == 0 && (states[0].getBlock() == Blocks.OBSIDIAN
                            || states[0].getBlock() == Blocks.BEDROCK
                            || states[0].getMaterial().isReplaceable()))
            {
                if (positions[0].equals(automine.getCurrent()))
                {
                    automine.setCurrent(null);
                }

                continue;
            }
            else if (i != 0
                && states[i].getBlock() == Blocks.OBSIDIAN
                && !automine.shouldMineObby())
            {
                return;
            }

            if (states[i].getBlock() != Blocks.AIR)
            {
                if (!MineUtil.canBreak(states[i], positions[i]))
                {
                    return;
                }

                attackPos = positions[i];
                valid = true;
            }
            else if (positions[i].equals(automine.getCurrent()))
            {
                automine.setCurrent(null);
            }
        }

        if (!valid)
        {
            return;
        }

        for (int i = 1; i < positions.length; i++)
        {
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class,
                    new AxisAlignedBB(positions[i])))
            {
                if (entity != null
                    && !(entity instanceof EntityItem)
                    && !EntityUtil.isDead(entity))
                {
                    valid = false;
                    return;
                }
            }

            if (automine.getNewVEntities())
            {
                break;
            }
        }

        BlockPos pos = positions[0];
        if (states[0].getBlock() != Blocks.OBSIDIAN
                && states[0].getBlock() != Blocks.BEDROCK)
        {
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class,
                    new AxisAlignedBB(pos)))
            {
                if (entity != null
                        && !EntityUtil.isDead(entity)
                        && entity.preventEntitySpawning
                        && !(entity instanceof EntityItem))
                {
                    valid = false;
                    return;
                }
            }
        }

        if (RotationUtil.getRotationPlayer().getDistanceSq(
                pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f)
                    >= automine.getBreakTrace()
            && !RayTraceUtil.canBeSeen(
                new Vec3d(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f),
                RotationUtil.getRotationPlayer()))
        {
            valid = false;
            return;
        }

        float self = DamageUtil.calculate(
            pos.getX() + 0.5f,
            pos.getY() + 1,
            pos.getZ() + 0.5f,
            RotationUtil.getRotationPlayer().getEntityBoundingBox(),
            RotationUtil.getRotationPlayer(),
            helper,
            true);

        if (!automine.isSuicide() && self > automine.getMaxSelfDmg())
        {
            this.valid = false;
            return;
        }

        if (target == null)
        {
            for (EntityPlayer player : mc.world.playerEntities)
            {
                if (player == null
                    || EntityUtil.isDead(player)
                    || Managers.FRIENDS.contains(player)
                    || player.getDistanceSq(pos) > 144)
                {
                    continue;
                }

                float d = DamageUtil.calculate(
                    pos.getX() + 0.5f,
                    pos.getY() + 1,
                    pos.getZ() + 0.5f,
                    player.getEntityBoundingBox(),
                    player,
                    helper,
                    true);

                if (d >= automine.getMinDmg())
                {
                    if (automine.getCurrent() == null)
                    {
                        automine.attackPos(attackPos);
                    }

                    return;
                }
            }
        }
        else if (!EntityUtil.isDead(target) && DamageUtil.calculate(
                                                pos.getX() + 0.5f,
                                                pos.getY() + 1,
                                                pos.getZ() + 0.5f,
                                                target.getEntityBoundingBox(),
                                                target,
                                                helper,
                                                true) >= automine.getMinDmg())
        {
            if (automine.getCurrent() == null)
            {
                automine.attackPos(attackPos);
            }

            return;
        }

        this.valid = false;
    }

    @Override
    public boolean isAffected(BlockPos pos, IBlockState state)
    {
        for (BlockPos position : positions)
        {
            if (position.equals(pos))
            {
                if (position.equals(automine.getCurrent()))
                {
                    automine.setCurrent(null);
                }

                blockStateChanges++;
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean isValid(IBlockAccess world, boolean checkPlayerState)
    {
        return blockStateChanges < positions.length * 2.25 && valid;
    }

    @Override
    public boolean cantBeImproved()
    {
        return !automine.canBigCalcsBeImproved();
    }

}
