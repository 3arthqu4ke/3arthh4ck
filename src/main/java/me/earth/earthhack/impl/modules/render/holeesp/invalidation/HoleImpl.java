package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import java.util.Objects;

class HoleImpl implements Hole
{
    private boolean valid = true;
    private final int x;
    private final int y;
    private final int z;
    private final int maxX;
    private final int maxZ;
    private final boolean _2x1;
    private final boolean _2x2;
    private final boolean safe;

    public HoleImpl(int x, int y, int z, int maxX, int maxZ, boolean is2x1, boolean is2x2, boolean safe)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this._2x1 = is2x1;
        this._2x2 = is2x2;
        this.safe = safe;
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public int getZ()
    {
        return z;
    }

    @Override
    public int getMaxX()
    {
        return maxX;
    }

    @Override
    public int getMaxZ()
    {
        return maxZ;
    }

    @Override
    public boolean isSafe()
    {
        return safe;
    }

    @Override
    public boolean is2x1()
    {
        return _2x1;
    }

    @Override
    public boolean is2x2()
    {
        return _2x2;
    }

    @Override
    public void invalidate()
    {
        valid = false;
    }

    @Override
    public boolean isValid()
    {
        return valid;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof HoleImpl)) return false;
        HoleImpl hole = (HoleImpl) o;
        return isValid() == hole.isValid()
                && getX() == hole.getX()
                && getY() == hole.getY()
                && getZ() == hole.getZ()
                && getMaxX() == hole.getMaxX()
                && getMaxZ() == hole.getMaxZ()
                && is2x1() == hole.is2x1()
                && is2x2() == hole.is2x2()
                && isSafe() == hole.isSafe();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(isValid(), getX(), getY(), getZ(), getMaxX(), getMaxZ(), is2x1(), is2x2(), isSafe());
    }

    @Override
    public String toString()
    {
        return "HoleImpl{" +
                "valid=" + valid +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", maxX=" + maxX +
                ", maxZ=" + maxZ +
                ", _2x1=" + _2x1 +
                ", _2x2=" + _2x2 +
                ", safe=" + safe +
                '}';
    }

}
