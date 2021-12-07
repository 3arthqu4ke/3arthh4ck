package me.earth.earthhack.impl.util.math.geocache;

import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.util.math.Vec3i;

public abstract class AbstractGeoCache implements GeoCache
{
    private final Vec3i[] cache;
    private final int[] indices;

    public AbstractGeoCache(int expectedSize, int indicesSize)
    {
        this.cache   = new Vec3i[expectedSize];
        this.indices = new int[indicesSize];
    }

    protected abstract void fill(Vec3i[] cache, int[] indices);

    @Override
    public void cache()
    {
        Vec3i dummy = new Vec3i(Integer.MAX_VALUE, 0, 0);
        this.cache[this.cache.length - 1] = dummy;
        fill(this.cache, this.indices);
        if (this.cache[this.cache.length - 1] == dummy)
        {
            throw new IllegalStateException("Cache was not filled!");
        }
    }

    @Override
    public int getRadius(double radius)
    {
        return indices[MathUtil.clamp((int) Math.ceil(radius), 0, indices.length)];
    }

    @Override
    public Vec3i get(int index)
    {
        return cache[index];
    }

    @Override
    public Vec3i[] array()
    {
        return cache;
    }

}
