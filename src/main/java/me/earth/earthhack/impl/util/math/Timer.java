package me.earth.earthhack.impl.util.math;

public class Timer
{
    private long startTime;

    public Timer() {
        startTime = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - startTime;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
    }

    public void adjust(int by) {
        startTime += by;
    }

}
