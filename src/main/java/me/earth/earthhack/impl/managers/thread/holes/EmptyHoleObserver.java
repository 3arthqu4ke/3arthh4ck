package me.earth.earthhack.impl.managers.thread.holes;

public class EmptyHoleObserver implements HoleObserver
{
    @Override
    public double getRange()    { return 0; }

    @Override
    public int getSafeHoles()   { return 0; }

    @Override
    public int getUnsafeHoles() { return 0; }

    @Override
    public int get2x1Holes()    { return 0; }

    @Override
    public int get2x2Holes()    { return 0; }
}
