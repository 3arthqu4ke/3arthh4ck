package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import me.earth.earthhack.impl.core.ducks.world.IChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Finds holes which could have been created when a block was placed.
 */
public class BlockHoleFinder extends HoleFinder
{
    private static final Vec3i[] OFFSETS = new Vec3i[]
            {
                    // When a block is added at 'p' we check these x offsets:
                    // this makes use of the fact that our hole check function only
                    // finds 2x1 holes and 2x2 holes on the bottom left air block.
                    //    x x
                    //  x x p x    and at 4 positions y + 1:   x x
                    //  x x x x                                x x
                    //    x x
                    // I attempted to order the offsets in a way where the ones that
                    // invalidate most of the other offsets if they are a hole come first.
                    new Vec3i(-1, 0, -1),
                    new Vec3i(0, 0, -1),
                    new Vec3i(-1, 0, 0),
                    new Vec3i(-1, 1, -1),
                    new Vec3i(-1, 0, -2),
                    new Vec3i(-2, 0, -1),
                    new Vec3i(-1, 0, 1),
                    new Vec3i(1, 0, -1),
                    new Vec3i(-1, 1, 0),
                    new Vec3i(0, 1, -1),
                    new Vec3i(0, 0, 1),
                    new Vec3i(-2, 0, 0),
                    new Vec3i(1, 0, 0),
                    new Vec3i(0, 0, -2),
                    new Vec3i(0, 1, 0)
            };

    private IChunk chunk;
    private int x;
    private int y;
    private int z;

    public BlockHoleFinder(HoleManager h)
    {
        super(h, h.getHoles(), h.get1x1(), h.get1x1Unsafe(), h.get2x1(), h.get2x2(),
                new MutPos(), null, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public void calcHoles()
    {
        for (Vec3i off : OFFSETS)
        {
            Hole hole = map.get(pos.setPos(getX() + off.getX(), getY() + off.getY(), getZ() + off.getZ()));
            if (hole == null || !hole.isValid())
            {
                calcHole();
            }
        }
    }

    public void setPos(BlockPos pos)
    {
        setX(pos.getX());
        setY(pos.getY());
        setZ(pos.getZ());
    }

    @Override
    public IChunk getChunk()
    {
        return chunk;
    }

    public void setChunk(IChunk chunk)
    {
        this.chunk = chunk;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getZ()
    {
        return z;
    }

    public void setZ(int z)
    {
        this.z = z;
    }

}
