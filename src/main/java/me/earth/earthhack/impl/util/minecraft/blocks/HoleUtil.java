package me.earth.earthhack.impl.util.minecraft.blocks;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.HashSet;
import java.util.Set;

import static me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil.isAir;
import static net.minecraft.util.EnumFacing.HORIZONTALS;

/**
 * Utility for calculating Holes.
 */
// TODO: prevent holes that cant be entered better, low prio 3vt can rage
public class HoleUtil implements Globals
{
    /** Offsets for a 2x2 hole. */
    private static final Vec3i[] OFFSETS_2x2 = new Vec3i[]
    {
        new Vec3i(0, 0, 0),
        new Vec3i(1, 0, 0),
        new Vec3i(0, 0, 1),
        new Vec3i(1, 0, 1)
    };

    /** Blocks that are blast resistant. Not all of them ofc. */
    public static final Set<Block> NO_BLAST = Sets.newHashSet(
        Blocks.BEDROCK,
        Blocks.OBSIDIAN,
        Blocks.ANVIL,
        Blocks.ENDER_CHEST
    );

    public static final Set<Block> UNSAFE = Sets.newHashSet(
        Blocks.OBSIDIAN,
        Blocks.ANVIL,
        Blocks.ENDER_CHEST
    );

    /**
     * Returns a boolean array of length 2,
     * where index 0 represents if the given
     * position is a hole and index 1 represents
     * if the given hole is safe (full bedrock) or
     * not.
     *
     * @param pos the given position.
     * @return a boolean array.
     */
    public static boolean[] isHole(BlockPos pos, boolean above)
    {
        boolean[] result = new boolean[]{false, true};
        if (!isAir(pos) || !isAir(pos.up()) || above && !isAir(pos.up(2)))
        {
            return result;
        }

        return is1x1(pos, result);
    }

    public static boolean[] is1x1(BlockPos pos)
    {
        return is1x1(pos, new boolean[]{false, true});
    }

    public static boolean[] is1x1(BlockPos pos, boolean[] result)
    {
        for (EnumFacing facing : EnumFacing.values())
        {
            if (facing != EnumFacing.UP)
            {
                BlockPos offset = pos.offset(facing);
                IBlockState state = mc.world.getBlockState(offset);
                if (state.getBlock() != Blocks.BEDROCK)
                {
                    if (!NO_BLAST.contains(state.getBlock()))
                    {
                        return result;
                    }

                    result[1] = false;
                }
            }
        }

        result[0] = true;
        return result;
    }

    /**
     * Returns <tt>true</tt> if the given position
     * is a long (2x1) hole.
     *
     * @param pos the given position.
     * @return <tt>true</tt> if a 2x1 hole.
     */
    public static boolean is2x1(BlockPos pos)
    {
        return is2x1(pos, true);
    }

    /**
     * Returns <tt>true</tt> if the given position
     * is a long (2x1) hole.
     *
     * @param pos the given position.
     * @return <tt>true</tt> if a 2x1 hole.
     */
    public static boolean is2x1(BlockPos pos, boolean upper)
    {
        if (upper && (!isAir(pos) || !isAir(pos.up()) || isAir(pos.down())))
        {
            return false;
        }

        int airBlocks = 0;
        for (EnumFacing facing : HORIZONTALS)
        {
            BlockPos offset = pos.offset(facing);
            if (isAir(offset))
            {
                if (isAir(offset.up()))
                {
                    if (!isAir(offset.down()))
                    {
                        for (EnumFacing offsetFacing: HORIZONTALS)
                        {
                            if (offsetFacing != facing.getOpposite())
                            {
                                IBlockState state = mc.world.getBlockState(
                                        offset.offset(offsetFacing));

                                if (!NO_BLAST.contains(state.getBlock()))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }

                airBlocks++;
            }

            if (airBlocks > 1)
            {
                return false;
            }
        }

        return airBlocks == 1;
    }

    /**
     * Returns <tt>true</tt> if the given position
     * is part of a 2x2 hole, more accurately if the
     * given position is the bottom left part of the
     * hole. The other 3 positions will have the
     * offsets (0,0,1), (1,0,0) and (1,0,1).
     *
     * @param pos the given position.
     * @return <tt>true</tt> if a 2x2 hole.
     */
    public static boolean is2x2Partial(BlockPos pos)
    {
        Set<BlockPos> positions = new HashSet<>();
        for (Vec3i vec : OFFSETS_2x2)
        {
            positions.add(pos.add(vec));
        }

        boolean airBlock = false;
        for (BlockPos holePos : positions)
        {
            if (isAir(holePos) && isAir(holePos.up()) && !isAir(holePos.down()))
            {
                if (isAir(holePos.up(2)))
                {
                    airBlock = true;
                }

                for (EnumFacing facing : HORIZONTALS)
                {
                    BlockPos offset = holePos.offset(facing);
                    if (!positions.contains(offset))
                    {
                        IBlockState state = mc.world.getBlockState(offset);
                        if (!NO_BLAST.contains(state.getBlock()))
                        {
                            return false;
                        }
                    }
                }
            }
            else
            {
                return false;
            }
        }

        return airBlock;
    }

    /**
     * Opposed to {@link HoleUtil#is2x2Partial(BlockPos)}, this method
     * will return <tt>true</tt> if the given Position is part
     * of a 2x2 hole even if its not the bottom left part.
     *
     * @param pos the position to check.
     * @return <tt>true</tt> if the position is part of a 2x2 hole.
     */
    public static boolean is2x2(BlockPos pos)
    {
        return is2x2(pos, true);
    }

    /**
     * Opposed to {@link HoleUtil#is2x2Partial(BlockPos)}, this method
     * will return <tt>true</tt> if the given Position is part
     * of a 2x2 hole even if its not the bottom left part.
     *
     * @param pos the position to check.
     * @return <tt>true</tt> if the position is part of a 2x2 hole.
     */
    public static boolean is2x2(BlockPos pos, boolean upper)
    {
        if (upper && !isAir(pos))
        {
            return false;
        }

        if (is2x2Partial(pos))
        {
            return true;
        }

        BlockPos l = pos.add(-1, 0, 0);
        boolean airL = isAir(l);
        if (airL && is2x2Partial(l))
        {
            return true;
        }

        BlockPos r = pos.add(0, 0, -1);
        boolean airR = isAir(r);
        if (airR && is2x2Partial(r))
        {
            return true;
        }

        return (airL || airR) && is2x2Partial(pos.add(-1, 0, -1));
    }

}
