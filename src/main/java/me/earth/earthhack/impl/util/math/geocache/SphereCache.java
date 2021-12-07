package me.earth.earthhack.impl.util.math.geocache;

import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.TreeSet;

// TODO: replace Sphere, but too lazy
public class SphereCache extends AbstractSphere
{
    private static final SphereCache INSTANCE = new SphereCache();

    static
    {
        INSTANCE.cache();
    }

    private SphereCache()
    {
        super(4187707, 101, 100.0);
    }

    public static SphereCache getInstance()
    {
        return INSTANCE;
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

            int compare = Double.compare(middle.distanceSq(o),
                                         middle.distanceSq(p));
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
