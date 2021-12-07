package me.earth.earthhack.impl.util.misc.intintmap;

/**
 * {@see http://java-performance.info/implementing-world-fastest-java-int-to-int-hash-map/}
 * {@author Mikhail Vorontsov}
 */
public interface IntIntMap
{
    int get(final int key);

    int put(final int key, final int value);

    int remove(final int key);

    int size();
}
