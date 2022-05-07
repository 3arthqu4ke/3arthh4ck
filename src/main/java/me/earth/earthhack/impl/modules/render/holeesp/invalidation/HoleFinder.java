package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.world.IChunk;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Finds all holes in a chunk.
 */
public class HoleFinder implements Runnable, Globals
{
    //     x <-
    //   x a x
    //     x
    private static final Vec3i[] OFFSETS_1x1 = new Vec3i[]
            {
                    new Vec3i(0, 0, 0),
                    new Vec3i(0, 0, -1),
                    new Vec3i(1, 0, -1),
                    new Vec3i(-1, 0, -1),
                    new Vec3i(0, 0, -2),
                    new Vec3i(0, -1, -1),
                    new Vec3i(0, 1, -1),
                    new Vec3i(0, 2, -1)
            };

    //     x x <-
    //   x a a x
    //     x x
    private static final Vec3i[] OFFSETS_2x1_x = new Vec3i[]
            {
                    new Vec3i(0, 0, 0),
                    new Vec3i(0, 0, -1),
                    new Vec3i(1, 0, -1),
                    new Vec3i(-1, 0, -1),
                    new Vec3i(0, 0, -2),
                    new Vec3i(0, -1, -1),
                    new Vec3i(0, 1, -1),
                    new Vec3i(0, 2, -1),
                    // same as 1x1 we just need to check these
                    new Vec3i(-1, 0, 0),   //    -> x x
                    new Vec3i(-2, 0, -1),  //  -> x y a x and above/under the one with y
                    new Vec3i(-1, 0, -2),  //    -> x x
                    new Vec3i(-1, -1, -1),
                    new Vec3i(-1, 1, -1),
                    new Vec3i(-1, 2, -1),
            };

    //      x
    //    x a x <-
    //    x a x
    //      x
    private static final Vec3i[] OFFSETS_2x1_z = new Vec3i[]
            {
                    new Vec3i(0, 0, 0),
                    new Vec3i(-1, -1, 0),
                    new Vec3i(-1, 0, 0),
                    new Vec3i(-1, 1, 0),
                    new Vec3i(-1, 2, 0),
                    new Vec3i(-2, 0, 0),
                    new Vec3i(-1, 0, 1), // top
                    new Vec3i(0, 0, -1),
                    new Vec3i(-1, -1, -1),
                    new Vec3i(-1, 0, -1),
                    new Vec3i(-1, 1, -1),
                    new Vec3i(-1, 2, -1),
                    new Vec3i(-2, 0, -1),
                    new Vec3i(-1, 0, -2)
            };

    //      x x
    //    x a a x <-
    //    x a a x
    //      x x
    private static final Vec3i[] OFFSETS_2x2 = new Vec3i[]
            {
                    new Vec3i(0, 0, 0),
                    new Vec3i(-1, -1, 0),
                    new Vec3i(-1, 0, 0),
                    new Vec3i(-1, 1, 0),
                    new Vec3i(-1, 2, 0),
                    new Vec3i(-2, 0, 0),
                    new Vec3i(-1, 0, 1), // top
                    new Vec3i(0, 0, -1),
                    new Vec3i(-1, -1, -1),
                    new Vec3i(-1, 0, -1),
                    new Vec3i(-1, 1, -1),
                    new Vec3i(-1, 2, -1),
                    new Vec3i(-2, 0, -1),
                    new Vec3i(-1, 0, -2),
                    // same as 2x1z we just need to check these
                    new Vec3i(-2, 0, 1),   //    -> x x
                    new Vec3i(-3, 0, 0),   //  -> x y a x  and above/under the ones with y
                    new Vec3i(-3, 0, -1),  //  -> x y a x
                    new Vec3i(-2, 0, -2),  //    -> x x
                    new Vec3i(-2, -1, 0),
                    new Vec3i(-2, 1, 0),
                    new Vec3i(-2, 2, 0),
                    new Vec3i(-2, -1, -1),
                    new Vec3i(-2, 1, -1),
                    new Vec3i(-2, 2, -1),
            };

    protected final HoleManager holeManager;
    protected final Map<BlockPos, Hole> map;
    protected final List<Hole> _1x1_safe;
    protected final List<Hole> _1x1_unsafe;
    protected final List<Hole> _2x1;
    protected final List<Hole> _2x2;
    protected final MutPos pos;

    private final IChunk chunk;
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    private final int minZ;
    private final int maxZ;

    public HoleFinder(Chunk chunk, int height, HoleManager holeManager)
    {
        this(holeManager, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new MutPos(),
                (IChunk) chunk, chunk.x * 16, chunk.x * 16 + 16, 0, height, chunk.z * 16, chunk.z * 16 + 16);
    }

    public HoleFinder(HoleManager holeManager, Map<BlockPos, Hole> map, List<Hole> _1x1_safe,
                      List<Hole> _1x1_unsafe, List<Hole> _2x1,
                      List<Hole> _2x2, MutPos pos, IChunk chunk,
                      int minX, int maxX,
                      int minY, int maxY,
                      int minZ, int maxZ)
    {
        this.holeManager = holeManager;
        this.map = map;
        this._1x1_safe = _1x1_safe;
        this._1x1_unsafe = _1x1_unsafe;
        this._2x1 = _2x1;
        this._2x2 = _2x2;
        this.pos = pos;
        this.chunk = chunk;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    @Override
    public void run()
    {
        long time = System.currentTimeMillis();
        calcHoles();
        mc.addScheduledTask(() ->
        {
            getChunk().setCompilingHoles(false);
            holeManager.get1x1Unsafe().addAll(_1x1_unsafe);
            holeManager.get1x1().addAll(_1x1_safe);
            holeManager.get2x1().addAll(_2x1);
            holeManager.get2x2().addAll(_2x2);
            map.forEach((pos, hole) -> {
                Hole before = holeManager.getHoles().put(pos, hole);
                if (before != null && before.isAirPart(pos)) {
                    before.invalidate();
                }
            });
        });

        Earthhack.getLogger().debug(String.format(
                "Compiling chunk %d, %d took %dms, found %d holes, %d unsafe, %d 2x1, %d 2x2",
                minX, minZ, System.currentTimeMillis() - time, _1x1_safe.size(),
                _1x1_unsafe.size(), _2x1.size(), _2x2.size()));
    }

    public void calcHoles()
    {
        for (int x = getMinX(); x < getMaxX(); x++)
        {
            for (int z = getMinZ(); z < getMaxZ(); z++)
            {
                for (int y = getMinY(); y <= getMaxY(); y++)
                {
                    pos.setPos(x, y, z);
                    if (!map.containsKey(pos))
                    {
                        calcHole();
                    }
                }
            }
        }
    }

    /**
     * Checks if {@link #pos} is the bottom left air block of a hole.
     */
    public void calcHole()
    {
        Boolean safe = checkAirAndFloor(pos, true, false);
        if (safe == null)
        {
            return;
        }

        boolean _2x1 = false;
        boolean _2x2;
        // -> x a
        pos.setX(pos.getX() - 1);
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.BEDROCK)
        {
            if (!HoleUtil.UNSAFE.contains(state.getBlock()))
            {
                return;
            }

            safe = false;
        }
        // x a
        //   x <-
        pos.setX(pos.getX() + 1);
        pos.setZ(pos.getZ() - 1);
        state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.BEDROCK)
        {
            if (!HoleUtil.UNSAFE.contains(state.getBlock()))
            {
                return;
            }

            safe = false;
        }
        //   x a x <-
        //     x
        pos.setX(pos.getX() + 1);
        pos.setZ(pos.getZ() + 1);
        state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.BEDROCK)
        {
            if (state.getBlock() == Blocks.AIR)
            {
                safe = checkAirAndFloor(pos, safe, true);
                if (safe == null)
                {
                    return;
                }

                _2x1 = true;
                //   x a a
                //     x x <-
                pos.setZ(pos.getZ() - 1);
                state = mc.world.getBlockState(pos);
                //noinspection DuplicatedCode
                if (!HoleUtil.NO_BLAST.contains(state.getBlock()))
                {
                    return;
                }

                pos.setZ(pos.getZ() + 1);
                pos.setX(pos.getX() + 1);
                //   x a a x <-
                //     x x
                state = mc.world.getBlockState(pos);
                if (!HoleUtil.NO_BLAST.contains(state.getBlock()))
                {
                    return;
                }

                //  Go back here v
                //           x a a x
                //             x x
                pos.setX(pos.getX() - 1);
            }
            else if (!HoleUtil.UNSAFE.contains(state.getBlock()))
            {
                return;
            }

            safe = false;
        }
        //  -> x
        //   x a x
        //     x
        pos.setX(pos.getX() - 1);
        pos.setZ(pos.getZ() + 1);
        state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.BEDROCK)
        {
            if (state.getBlock() == Blocks.AIR)
            {
                safe = checkAirAndFloor(pos, safe, true);
                if (safe == null)
                {
                    return;
                }

                _2x2 = _2x1;
                //
                // -> x a
                //    x a x
                //      x
                pos.setX(pos.getX() - 1);
                //noinspection DuplicatedCode
                state = mc.world.getBlockState(pos);
                if (!HoleUtil.NO_BLAST.contains(state.getBlock()))
                {
                    return;
                }

                //   -> x
                //    x a
                //    x a x
                //      x
                pos.setZ(pos.getZ() + 1);
                pos.setX(pos.getX() + 1);
                state = mc.world.getBlockState(pos);
                if (!HoleUtil.NO_BLAST.contains(state.getBlock()))
                {
                    return;
                }

                //      x                   x
                //    x a x <-    or      x a a <-
                //    x a x               x a a x
                //      x                   x x
                pos.setX(pos.getX() + 1);
                pos.setZ(pos.getZ() - 1);
                state = mc.world.getBlockState(pos);
                if (!HoleUtil.NO_BLAST.contains(state.getBlock()))
                {
                    if (state.getBlock() == Blocks.AIR && _2x2)
                    {
                        safe = checkAirAndFloor(pos, safe, true);
                        if (safe == null)
                        {
                            return;
                        }

                        //      x x <-
                        //    x a a
                        //    x a a x
                        //      x x
                        pos.setZ(pos.getZ() + 1);
                        state = mc.world.getBlockState(pos);
                        if (!HoleUtil.NO_BLAST.contains(state.getBlock()))
                        {
                            return;
                        }

                        //      x x
                        //    x a a x <-
                        //    x a a x
                        //      x x
                        pos.setZ(pos.getZ() - 1);
                        pos.setX(pos.getX() + 1);
                        state = mc.world.getBlockState(pos);
                        if (!HoleUtil.NO_BLAST.contains(state.getBlock()))
                        {
                            return;
                        }

                        Hole hole = new HoleImpl(getChunk(), pos.getX() - 2, pos.getY(), pos.getZ() - 1, pos.getX(), pos.getZ() + 1,
                                false, true, false);
                        this._2x2.add(hole);
                        for (Vec3i offset : OFFSETS_2x2)
                        {
                            putHole(pos.add(offset), hole);
                        }
                    }

                    return;
                }

                if (_2x1)
                {
                    return;
                }

                //      x
                //    x a x <- reminder, we are here
                //    x a x
                //      x
                Hole hole = new HoleImpl(getChunk(), pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX(), pos.getZ() + 1, true,
                        false, false);
                this._2x1.add(hole);
                for (Vec3i offset : OFFSETS_2x1_z)
                {
                    putHole(pos.add(offset), hole);
                }

                return;
            }
            else if (!HoleUtil.UNSAFE.contains(state.getBlock()))
            {
                return;
            }

            safe = false;
        }

        if (_2x1)
        {
            //     x x <-
            //   x a a x
            //     x x
            pos.setX(pos.getX() + 1);
            state = mc.world.getBlockState(pos);
            if (HoleUtil.NO_BLAST.contains(state.getBlock()))
            {
                Hole hole = new HoleImpl(getChunk(), pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getZ(),
                        true, false, false);
                this._2x1.add(hole);
                for (Vec3i offset : OFFSETS_2x1_x)
                {
                    putHole(pos.add(offset), hole);
                }
            }

            return;
        }
        //     x <- reminder, we are here rn
        //   x a x
        //     x
        Hole hole = new HoleImpl(getChunk(), pos.getX(), pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getZ(), false, false,
                safe);
        (safe ? _1x1_safe : _1x1_unsafe).add(hole);
        for (Vec3i offset : OFFSETS_1x1)
        {
            putHole(pos.add(offset), hole);
        }
    }

    private Boolean checkAirAndFloor(MutPos pos, boolean safe, boolean alreadyCheckedFirst)
    {
        if (alreadyCheckedFirst)
        {
            pos.setY(pos.getY() + 1);
        }

        if (checkAir(pos) || checkAir(pos) || !alreadyCheckedFirst && checkAir(pos))
        {
            return null;
        }

        pos.setY(pos.getY() - 4);
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.BEDROCK)
        {
            if (!HoleUtil.UNSAFE.contains(state.getBlock()))
            {
                return null;
            }

            pos.setY(pos.getY() + 1);
            return Boolean.FALSE;
        }

        pos.setY(pos.getY() + 1);
        return safe;
    }

    private boolean checkAir(MutPos pos)
    {
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.AIR)
        {
            return true;
        }

        pos.setY(pos.getY() + 1);
        return false;
    }


    private void putHole(BlockPos pos, Hole hole) {
        Hole before = map.put(pos.toImmutable(), hole);
        if (before != null && before.isAirPart(pos)) {
            before.invalidate();
        }
    }

    public IChunk getChunk()
    {
        return chunk;
    }

    public int getMinX()
    {
        return minX;
    }

    public int getMaxX()
    {
        return maxX;
    }

    public int getMinY()
    {
        return minY;
    }

    public int getMaxY()
    {
        return maxY;
    }

    public int getMinZ()
    {
        return minZ;
    }

    public int getMaxZ()
    {
        return maxZ;
    }

}
