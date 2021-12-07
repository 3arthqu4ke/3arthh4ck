package me.earth.earthhack.impl.modules.render.newchunks.util;

public class ChunkData
{
    private final int x;
    private final int z;

    public ChunkData(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    public int getX()
    {
        return x;
    }

    public int getZ()
    {
        return z;
    }

    @Override
    public int hashCode()
    {
        int hash = 23;
        hash = hash * 31 + x;
        hash = hash * 31 + z;
        return hash;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof ChunkData)
        {
            return Double.compare(((ChunkData) o).x, x) == 0
                    && Double.compare(((ChunkData) o).z, z) == 0;
        }
        else
        {
            return super.equals(o);
        }
    }

}
