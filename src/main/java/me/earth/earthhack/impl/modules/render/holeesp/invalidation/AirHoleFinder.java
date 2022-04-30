package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import me.earth.earthhack.impl.core.ducks.world.IChunk;
import net.minecraft.util.math.BlockPos;

/**
 * Finds holes which could've been created when a block turned into air.
 */
public class AirHoleFinder extends HoleFinder
{
    private IChunk chunk;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;

    public AirHoleFinder(HoleManager h)
    {
        super(h, h.getHoles(), h.get1x1(), h.get1x1Unsafe(), h.get2x1(), h.get2x2(),
                new MutPos(), null, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public void calcHoles()
    {
        for (int x = getMinX(); x < getMaxX(); x++)
        {
            for (int z = getMinZ(); z < getMaxZ(); z++)
            {
                for (int y = getMinY(); y <= getMaxY(); y++)
                {
                    Hole hole = map.get(pos.setPos(x, y, z));
                    if (hole == null || !hole.isValid())
                    {
                        calcHole();
                    }
                }
            }
        }
    }

    public void setPos(BlockPos pos)
    {
        // we calculate a 3 block deep, 2x2 cuboid
        //    x a <- this block turned into air
        //    x x
        // because we can make use of the fact that the holecalc only uses the bottom left airblock of the hole.
        // that means this 2x2x3 cubicle will find all holes which could've been created by the pos becoming air.
        setMaxX(pos.getX() + 1);
        setMinX(pos.getX() - 1);
        setMaxY(pos.getY());
        setMinY(pos.getY() - 2);
        setMaxZ(pos.getZ() + 1);
        setMinZ(pos.getZ() - 1);
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

    @Override
    public int getMinX()
    {
        return minX;
    }

    public void setMinX(int minX)
    {
        this.minX = minX;
    }

    @Override
    public int getMaxX()
    {
        return maxX;
    }

    public void setMaxX(int maxX)
    {
        this.maxX = maxX;
    }

    @Override
    public int getMinY()
    {
        return minY;
    }

    public void setMinY(int minY)
    {
        this.minY = minY;
    }

    @Override
    public int getMaxY()
    {
        return maxY;
    }

    public void setMaxY(int maxY)
    {
        this.maxY = maxY;
    }

    @Override
    public int getMinZ()
    {
        return minZ;
    }

    public void setMinZ(int minZ)
    {
        this.minZ = minZ;
    }

    @Override
    public int getMaxZ()
    {
        return maxZ;
    }

    public void setMaxZ(int maxZ)
    {
        this.maxZ = maxZ;
    }
}
