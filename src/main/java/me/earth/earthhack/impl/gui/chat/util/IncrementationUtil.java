package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.pingbypass.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility for Incrementing numbers for commands and gui.
 */
public class IncrementationUtil
{
    /** {@link Keyboard#getRControl()} maxes out. */
    public static final int MAX    = Keyboard.getRControl();
    /** {@link Keyboard#getLControl()} increments faster. */
    public static final int FASTER = Keyboard.getLControl();
    /** {@link Keyboard#getLMenu()} increments fast. */
    public static final int FAST   = Keyboard.getLMenu();

    /**
     * Uses Strings to prevent floating,
     * since this is Utility for commands and gui.
     * <p></p>
     * <p> In/Decrements the given double by:
     * <p>- 0.1 by default,
     * <p>- 1.0 if {@link Keyboard#KEY_LCONTROL} is down,
     * <p>- 10% if {@link Keyboard#KEY_LMENU} and {@link Keyboard#KEY_LCONTROL}
     * are down,
     * <p>- the given Max/Min Value if {@link Keyboard#KEY_RCONTROL} is down,
     * <p>- 10.0 if {@link Keyboard#KEY_LMENU} is down.
     *
     * @param d String representing the number to in/decrement.
     * @param min the minimum value to return.
     * @param max the maximum value to return.
     * @param de <tt>true</tt> if decrementing.
     * @return a String representing the value de/incremented,
     *         based on the keys pressed.
     */
    public static String crD(String d, String min, String max, boolean de)
    {
        //noinspection DuplicatedCode
        if (Keyboard.isKeyDown(MAX))
        {
            if (de)
            {
                return min;
            }
            else
            {
                return max;
            }
        }

        BigDecimal v = new BigDecimal(d);
        BigDecimal n = new BigDecimal(min);
        BigDecimal m = new BigDecimal(max);
        BigDecimal incr;

        if (Keyboard.isKeyDown(FASTER))
        {
            if (Keyboard.isKeyDown(FAST))
            {
                BigDecimal diff = m.subtract(n);
                incr = diff.divide(new BigDecimal(de ? "-10" : "10"),
                                    RoundingMode.FLOOR);
            }
            else
            {
                incr = new BigDecimal(de ? "-1.0" : "1.0");
            }
        }
        else if (Keyboard.isKeyDown(FAST))
        {
            incr = new BigDecimal(de ? "-10.0" : "10.0");
        }
        else
        {
            incr = new BigDecimal(de ? "-0.1" : "0.1");
        }

        return MathUtil.clamp(v.add(incr), n, m).toString();
    }

    /**
     * Same as {@link IncrementationUtil#crD(String, String, String, boolean)}
     * but not rounded.
     * <p></p>
     * <p> In/Decrements the given double by:
     * <p>- 0.1 by default,
     * <p>- 1.0 if {@link Keyboard#KEY_LCONTROL} is down,
     * <p>- 10% if {@link Keyboard#KEY_LMENU} and {@link Keyboard#KEY_LCONTROL}
     * are down,
     * <p>- the given Max/Min Value if {@link Keyboard#KEY_RCONTROL} is down,
     * <p>- 10.0 if {@link Keyboard#KEY_LMENU} is down.
     *
     * @param d the number to in/decrement.
     * @param min the minimum value to return.
     * @param max the maximum value to return.
     * @param de <tt>true</tt> if decrementing.
     * @return a the number, in/decremented.
     */
    public static double crF(double d, double min, double max, boolean de)
    {
        //noinspection DuplicatedCode
        if (Keyboard.isKeyDown(MAX))
        {
            if (de)
            {
                return min;
            }
            else
            {
                return max;
            }
        }

        BigDecimal v = new BigDecimal(d);
        BigDecimal n = new BigDecimal(min);
        BigDecimal m = new BigDecimal(max);
        BigDecimal incr;

        if (Keyboard.isKeyDown(FASTER))
        {
            if (Keyboard.isKeyDown(FAST))
            {
                BigDecimal diff = m.subtract(n);
                incr = diff.divide(new BigDecimal(de ? "-10" : "10"),
                        RoundingMode.FLOOR);
            }
            else
            {
                incr = new BigDecimal(de ? "-1.0" : "1.0");
            }
        }
        else if (Keyboard.isKeyDown(FAST))
        {
            incr = new BigDecimal(de ? "-10.0" : "10.0");
        }
        else
        {
            incr = new BigDecimal(de ? "-0.1" : "0.1");
        }

        return MathUtil.clamp(v.add(incr), n, m).doubleValue();
    }

    /**
     * In/Decrements the given double by:
     * <p></p>
     * <p>- 1 by default,
     * <p>- 5% if {@link Keyboard#KEY_LCONTROL} is down,
     * <p>- 10% if {@link Keyboard#KEY_LMENU} and {@link Keyboard#KEY_LCONTROL}
     * are down,
     * <p>- the given Max/Min Value if {@link Keyboard#KEY_RCONTROL} is down,
     * <p>- 10 if {@link Keyboard#KEY_LMENU} is down.
     *
     * @param l the number to in/decrement.
     * @param min the minimum value to return.
     * @param max the maximum value to return.
     * @param de <tt>true</tt> if decrementing.
     * @return the value de/incremented, based on the keys pressed.
     */
    public static long crL(long l, long min, long max, boolean de)
    {
        if (Keyboard.isKeyDown(MAX))
        {
            if (de)
            {
                return min;
            }
            else
            {
                return max;
            }
        }

        long incr;

        if (Keyboard.isKeyDown(FASTER))
        {
            long diff = max - min;
            if (Keyboard.isKeyDown(FAST))
            {
                incr = (diff / 10);
            }
            else
            {
                incr = (diff / 5);
            }
        }
        else if (Keyboard.isKeyDown(FAST))
        {
            incr = 10;
        }
        else
        {
            incr = 1;
        }

        return MathUtil.clamp(l + (de ? -incr : incr), min, max);
    }

}
