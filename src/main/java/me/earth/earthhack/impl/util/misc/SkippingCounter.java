package me.earth.earthhack.impl.util.misc;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * A counter that skips numbers that test <tt>false</tt>
 * with the given {@link Predicate}.
 *
 * A predicate that always returns <tt>false</tt> will
 * cause a while loop to run infinitely!
 */
public class SkippingCounter
{
    private final AtomicInteger counter;
    private final Predicate<Integer> skip;
    private final int initial;

    /**
     * Note that initial will not be tested on the given predicate.
     *
     * @param initial the initial value of this counter.
     * @param skip the predicate that determines
     *             if a number should be skipped
     *             (if it returns <tt>false</tt>).
     */
    public SkippingCounter(int initial, Predicate<Integer> skip)
    {
        this.counter = new AtomicInteger(initial);
        this.initial = initial;
        this.skip    = skip;
    }

    /**
     * Note that the initial will not be tested on the given
     * predicate, so its possible to receive a number that would
     * test false with the given predicate with this method.
     *
     * @return the current value of this counter.
     */
    public int get()
    {
        return counter.get();
    }

    /**
     * @return the currentValue++ until the predicate returns <tt>true</tt>.
     */
    public int next()
    {
        int result;

        do
        {
            result = counter.incrementAndGet();
        }
        while (!skip.test(result));

        return result;
    }

    /**
     * Sets this counter back to the initially given value.
     * The value will not be tested.
     */
    public void reset()
    {
        counter.set(initial);
    }

}
