package me.earth.earthhack.impl.util.math;

/**
 * A simple implementation of the DiscreteTimer interface.
 * Using a StopWatch to prevent degeneration of the
 * timer after waiting for longer periods of time.
 */
public class GuardTimer implements DiscreteTimer
{
    private final StopWatch guard = new StopWatch();
    private final long interval;
    private final long guardDelay;
    private long delay;
    private long time;

    public GuardTimer()
    {
        this(1000);
    }

    public GuardTimer(long guardDelay)
    {
        this(guardDelay, 10);
    }

    public GuardTimer(long guardDelay, long interval)
    {
        this.guardDelay = guardDelay;
        this.interval = interval;
    }

    @Override
    public long getTime()
    {
        return System.currentTimeMillis() - time;
    }

    @Override
    public void setTime(long time)
    {
        this.time = time;
    }

    @Override
    public boolean passed(long ms)
    {
        return ms == 0
                || ms < interval
                || System.currentTimeMillis() - time >= ms;
    }

    @Override
    public DiscreteTimer reset(long ms)
    {
        if (ms <= interval || this.delay != ms || guard.passed(guardDelay))
        {
            this.delay = ms;
            reset();
        }
        else
        {
            time = ms + time;
        }

        return this;
    }

    /**
     * Hard resets this timer to System.currentTimeMillis()
     * and the underlying guard StopWatch.
     */
    public void reset()
    {
        time = System.currentTimeMillis();
        guard.reset();
    }

}
