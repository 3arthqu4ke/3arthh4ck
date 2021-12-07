package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ConstellationFactory implements Globals
{
    private ConstellationFactory() { throw new AssertionError(); }

    public static LegConstellation create(LegSwitch module,
                                          List<EntityPlayer> players)
    {
        return create(module, players, mc.world);
    }

    public static LegConstellation create(LegSwitch module,
                                          List<EntityPlayer> players,
                                          IBlockAccess access)
    {
        if (module.closest.getValue())
        {
            return getConstellation(module,
                                    EntityUtil.getClosestEnemy(players),
                                    access);
        }

        LegConstellation closest = null;
        double distance = Double.MAX_VALUE;
        LegConstellation closestBad = null;
        double badDistance = Double.MAX_VALUE;
        for (EntityPlayer player : players)
        {
            if (!EntityUtil.isValid(player, 12.0f))
            {
                continue;
            }

            double dist = mc.player.getDistanceSq(player);
            if (closest == null || distance > dist)
            {
                LegConstellation c = getConstellation(module, player, access);
                if (c != null)
                {
                    if (c.firstNeedsObby
                            || c.secondNeedsObby)
                    {
                        if (dist >= badDistance)
                        {
                            continue;
                        }

                        badDistance = dist;
                        closestBad  = c;
                        continue;
                    }

                    closest = c;
                    distance = dist;
                }
            }
        }

        return closest == null ? closestBad : closest;
    }

    private static LegConstellation getConstellation(LegSwitch module,
                                                     EntityPlayer player,
                                                     IBlockAccess access)
    {
        if (player == null
                || mc.player.getDistanceSq(player)
                        > MathUtil.square(module.targetRange.getValue()))
        {
            return null;
        }

        BlockPos playerPos = PositionUtil.getPosition(player);
        IBlockState playerPosState = access.getBlockState(playerPos);
        if (!playerPosState.getMaterial().isReplaceable())
        {
            return null;
        }

        LegConstellation result = null;
        LegConstellation closestBad = null;
        double distance = Double.MAX_VALUE;
        double badDistance = Double.MAX_VALUE;
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos offset = playerPos.offset(facing);
            double dist = BlockUtil.getDistanceSq(offset);
            if (dist >= distance)
            {
                continue;
            }

            IBlockState offsetState = access.getBlockState(offset);
            if (!offsetState.getMaterial().isReplaceable())
            {
                continue;
            }

            LegConstellation constellation = getConstellation(module,
                                                              offset,
                                                              facing,
                                                              player,
                                                              playerPos,
                                                              access);
            if (constellation != null)
            {
                constellation.add(playerPos, playerPosState);
                constellation.add(offset, offsetState);
                if (constellation.firstNeedsObby
                        || constellation.secondNeedsObby)
                {
                    if (dist >= badDistance)
                    {
                        continue;
                    }

                    badDistance = dist;
                    closestBad  = constellation;
                    continue;
                }

                distance = dist;
                result   = constellation;
            }
        }

        return result == null ? closestBad : result;
    }

    private static LegConstellation getConstellation(LegSwitch module,
                                                     BlockPos pos,
                                                     EnumFacing facing,
                                                     EntityPlayer target,
                                                     BlockPos playerPos,
                                                     IBlockAccess access)
    {
        EnumFacing[] rotated = MathUtil.getRotated(facing);
        int badStates = 0;
        Map<BlockPos, IBlockState> states = new HashMap<>();
        for (EnumFacing f : rotated)
        {
            BlockPos p = pos.offset(f);
            IBlockState pState = access.getBlockState(p);
            if (!pState.getMaterial().isReplaceable())
            {
                badStates++;
            }

            states.put(p, pState);
        }

        if (badStates > 1)
        {
            return null;
        }

        BlockPos mid = pos.offset(facing);
        if (module.requireMid.getValue() // Bad not needed!
                && !access.getBlockState(mid).getMaterial().isReplaceable())
        {
            return null;
        }

        BlockPos first  = mid.offset(rotated[0]).down();
        BlockPos second = mid.offset(rotated[1]).down();

        if (!module.checkPos(first) || !module.checkPos(second))
        {
            return null;
        }

        IBlockState firstState  = access.getBlockState(first);
        IBlockState secondState = access.getBlockState(second);

        int require = requiresObby(first, firstState,
                module.newVer.getValue(), module.newVerEntities.getValue());
        if (require == -1)
        {
            return null;
        }

        int require1 = requiresObby(second, secondState,
                module.newVer.getValue(), module.newVerEntities.getValue());
        if (require1 == -1)
        {
            return null;
        }

        IBlockAccess blockAccess = mc.world;
        if (require == 1 || require1 == 1)
        {
            BlockStateHelper helper = new BlockStateHelper();
            helper.addBlockState(first,  Blocks.OBSIDIAN.getDefaultState());
            helper.addBlockState(second, Blocks.OBSIDIAN.getDefaultState());
            blockAccess = helper;
        }

        EntityPlayer player = RotationUtil.getRotationPlayer();
        float self = DamageUtil.calculate(
                first.getX() + 0.5f,
                first.getY() + 1,
                first.getZ() + 0.5f,
                player.getEntityBoundingBox(),
                player,
                blockAccess, true);
        if (self > module.maxSelfDamage.getValue())
        {
            return null;
        }

        self = DamageUtil.calculate(
                second.getX() + 0.5f,
                second.getY() + 1,
                second.getZ() + 0.5f,
                player.getEntityBoundingBox(),
                player,
                blockAccess, true);
        if (self > module.maxSelfDamage.getValue())
        {
            return null;
        }

        if (DamageUtil.calculate(first, target)
                > module.minDamage.getValue()
                || DamageUtil.calculate(second, target)
                    > module.minDamage.getValue())
        {
            return new LegConstellation(target, pos, playerPos,
                    first, second, states, require == 1, require1 == 1);
        }

        return null;
    }

    private static int requiresObby(BlockPos pos,
                                    IBlockState state,
                                    boolean newVer,
                                    boolean newVerEntities)
    {
        int result = -1;
        if (state.getBlock() != Blocks.OBSIDIAN
                && state.getBlock() != Blocks.BEDROCK)
        {
            if (!state.getMaterial().isReplaceable())
            {
                return result;
            }

            result = 0;
        }

        BlockPos up   = pos.up();
        BlockPos upUp = up.up();
        if (mc.world.getBlockState(up).getBlock() != Blocks.AIR
            || !newVer
                && mc.world.getBlockState(upUp).getBlock() != Blocks.AIR
            || !BlockUtil.checkEntityList(up, true, null)
            || newVerEntities
                && !BlockUtil.checkEntityList(upUp, true, null))
        {
            return -1;
        }

        return ++result;
    }

}
