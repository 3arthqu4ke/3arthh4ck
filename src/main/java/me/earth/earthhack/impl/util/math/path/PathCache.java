package me.earth.earthhack.impl.util.math.path;

import me.earth.earthhack.impl.util.math.geocache.AbstractSphere;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.TreeSet;

public class PathCache extends AbstractSphere
{
    public PathCache(int expectedSize, int indicesSize, double radius)
    {
        super(expectedSize, indicesSize, radius);
    }

    @Override
    protected Collection<BlockPos> sorter(BlockPos middle)
    {
        return new TreeSet<>((o, p) ->
        {
            if (o.equals(p))
            {
                return 0;
            }

            // prioritize shortest paths
            int xpDiff = middle.getX() - p.getX();
            int ypDiff = middle.getY() - p.getY();
            int zpDiff = middle.getZ() - p.getZ();

            int xoDiff = middle.getX() - o.getX();
            int yoDiff = middle.getY() - o.getY();
            int zoDiff = middle.getZ() - o.getZ();

            int compare = Integer.compare(
                PathFinder.produceOffsets(false, false, xoDiff, yoDiff, zoDiff)
                        .length,
                PathFinder.produceOffsets(false, false, xpDiff, ypDiff, zpDiff)
                        .length);

            if (compare != 0)
            {
                return compare;
            }

            compare = Double.compare(middle.distanceSq(o),
                                     middle.distanceSq(p));
            //noinspection DuplicatedCode
            if (compare == 0)
            {
                // This prioritizes positions closer to an axis
                compare = Integer.compare(Math.abs(o.getX())
                                        + Math.abs(o.getY())
                                        + Math.abs(o.getZ()),
                                          Math.abs(p.getX())
                                        + Math.abs(p.getY())
                                        + Math.abs(p.getZ()));
            }

            return compare == 0 ? 1 : compare;
        });
    }

}
