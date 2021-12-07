package me.earth.earthhack.impl.util.math.geocache;

import me.earth.earthhack.impl.managers.thread.safety.SafetyManager;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.TreeSet;

/**
 * Caches a Sphere of radius 100 and indices required
 * to get Sub-Spheres with smaller radii. The sphere is
 * sorted by distance to the middle.
 *
 * This is much faster than the older methods,
 * especially in situations where we'd need to sort the positions or
 * where closer positions might get fast results first
 * ({@link PathFinder}, {@link SafetyManager}, etc.).
 */
public class Sphere
{
    private static final Vec3i[] SPHERE = new Vec3i[4187707];
    private static final int[] INDICES = new int[101];

    static
    {
        // Setting the last byte to a dummy value.
        // That way we can check if the sphere has been fully initialized.
        SPHERE[SPHERE.length - 1] = new Vec3i(Integer.MAX_VALUE, 0, 0);
    }

    /** This class is a Utility class and shouldn't be instantiated. */
    private Sphere() { throw new AssertionError(); }

    /**
     * Gives you an Index for the Sphere.
     * All Positions up to the returned index will lie within
     * the given radius. Since the radius is rounded up you will
     * have to check if the last indices really lie within your
     * radius. radii > 100 will always return {@link Sphere#getLength()}.
     *
     * @param radius the radius to get the max index for.
     * @return the maximum index for the given radius.
     */
    public static int getRadius(double radius)
    {
        return INDICES[MathUtil.clamp((int) Math.ceil(radius), 0, INDICES.length)];
    }

    /**
     * Gets the Vec3i from the sphere at the given index.
     *
     * @param index the index of the Vector.
     * @return Vec3i at the given index.
     * @throws IndexOutOfBoundsException if the given index
     *         lies outside 0 and {@link Sphere#getLength()}-1.
     */
    public static Vec3i get(int index)
    {
        return SPHERE[index];
    }

    /**
     * @return the length of the sphere array.
     */
    public static int getLength()
    {
        return SPHERE.length;
    }

    /**
     * Initializes the Sphere.
     * With Forge this is called on PreInit, as soon as possible.
     */
    public static void cacheSphere(Logger logger)
    {
        logger.info("Caching Sphere...");
        long time = System.currentTimeMillis();

        BlockPos pos = BlockPos.ORIGIN;
        Set<BlockPos> positions = new TreeSet<>((o, p) ->
        {
            if (o.equals(p))
            {
                return 0;
            }

            int compare = Double.compare(pos.distanceSq(o), pos.distanceSq(p));
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

        double r = 100.0;
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

        if (positions.size() != SPHERE.length)
        {
            throw new IllegalStateException("Unexpected Size for Sphere: "
                    + positions.size()
                    + ", expected "
                    + SPHERE.length
                    + "!");
        }

        int i = 0;
        int currentDistance = 0;
        for (BlockPos off : positions)
        {
            if (Math.sqrt(pos.distanceSq(off)) > currentDistance)
            {
                INDICES[currentDistance++] = i;
            }

            SPHERE[i++] = off;
        }

        if (currentDistance != INDICES.length - 1)
        {
            throw new IllegalStateException("Sphere Indices not initialized!");
        }

        INDICES[INDICES.length - 1] = SPHERE.length;
        if (SPHERE[SPHERE.length - 1].getX() == Integer.MAX_VALUE)
        {
            throw new IllegalStateException("Sphere wasn't filled!");
        }

        time = System.currentTimeMillis() - time;
        logger.info("Cached sphere in " + time + "ms.");
    }

}
