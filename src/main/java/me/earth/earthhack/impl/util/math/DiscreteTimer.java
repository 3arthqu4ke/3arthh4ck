package me.earth.earthhack.impl.util.math;

/**
 * A StopWatch that works
 * in environments with discrete delays(ticks).
 */
public interface DiscreteTimer extends Passable
{
    /**
     * Resets this timer. Passed will return
     * <tt>true</tt> until the delay has been
     * passed.
     *
     * @param delay the delay.
     * @return this.
     */
    DiscreteTimer reset(long delay);

    /**
     * @return time since last reset.
     */
    long getTime();

    void setTime(long time);

}
