package me.earth.earthhack.impl.util.math;

// TODO: why does this exist?
public class Timer
{
    private long startTime;

    public Timer() {
        startTime = System.currentTimeMillis();
    }

    public boolean passed(double ms) {
        return System.currentTimeMillis() - startTime >= ms;
    }

    public boolean passed(long ms) {
        return System.currentTimeMillis() - startTime >= ms;
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
