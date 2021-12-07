package me.earth.earthhack.impl.util.math.geocache;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Collection;

public abstract class AbstractSphere extends AbstractGeoCache
{
    private final double r;

    public AbstractSphere(int expectedSize, int indicesSize, double radius)
    {
        super(expectedSize, indicesSize);
        this.r = radius;
    }

    protected abstract Collection<BlockPos> sorter(BlockPos middle);

    @Override
    protected void fill(Vec3i[] cache, int[] indices)
    {
        BlockPos pos = BlockPos.ORIGIN;
        Collection<BlockPos> positions = sorter(pos);
        double rSquare = r * r;
        for (int x = pos.getX() - (int) r; x <= pos.getX() + r; x++)
        {
            for (int z = pos.getZ() - (int) r; z <= pos.getZ() + r; z++)
            {
                for (int y = pos.getY() - (int) r; y < pos.getY() + r; y++)
                {
                    double dist = (pos.getX() - x) * (pos.getX() - x)
                                + (pos.getZ() - z) * (pos.getZ() - z)
                                + (pos.getY() - y) * (pos.getY() - y);

                    if (dist < rSquare)
                    {
                        positions.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        if (positions.size() != cache.length)
        {
            throw new IllegalStateException("Unexpected Size for Sphere: "
                    + positions.size()
                    + ", expected "
                    + cache.length
                    + "!");
        }

        int i = 0;
        int currentDistance = 0;
        for (BlockPos off : positions)
        {
            if (Math.sqrt(pos.distanceSq(off)) > currentDistance)
            {
                indices[currentDistance++] = i;
            }

            cache[i++] = off;
        }

        if (currentDistance != indices.length - 1)
        {
            throw new IllegalStateException("Sphere Indices not initialized!");
        }

        indices[indices.length - 1] = cache.length;
    }

}
