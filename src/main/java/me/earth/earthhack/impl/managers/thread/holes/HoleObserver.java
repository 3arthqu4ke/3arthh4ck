package me.earth.earthhack.impl.managers.thread.holes;

/**
 * A HoleObserver that can be registered
 * with {@link HoleManager#register(HoleObserver)}.
 * HoleObservers are required for the HoleManager
 * to run, so that it doesn't calculate holes when
 * not needed.
 */
public interface HoleObserver extends Comparable<HoleObserver>
{
    /**
     * @return the minimum range in which holes should
     *         be checked while this HoleObserver is registered.
     */
    double getRange();

    /**
     * @return the Amount of safe holes that should be calculated.
     */
    int getSafeHoles();

    /**
     * @return the Amount of unsafe holes that should be calculated.
     */
    int getUnsafeHoles();

    /**
     * @return the Amount of 2x1 holes that should be calculated.
     */
    int get2x1Holes();

    /**
     * @return the Amount of 2x2 holes that should be calculated.
     */
    int get2x2Holes();

    default boolean isThisHoleObserverActive() {
        return true;
    }

    @Override
    default int compareTo(HoleObserver o)
    {
        return Double.compare(this.getRange(), o.getRange());
    }

}
