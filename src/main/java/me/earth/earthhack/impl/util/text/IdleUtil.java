package me.earth.earthhack.impl.util.text;

import me.earth.earthhack.impl.util.math.StopWatch;

public class IdleUtil
{
    private static final StopWatch dotTimer = new StopWatch();
    private static final StopWatch undTimer = new StopWatch();

    private static String dots = "";

    public static String getDots()
    {
        if (dotTimer.passed(500))
        {
            dots += ".";
            dotTimer.reset();
        }

        if (dots.length() > 3)
        {
            dots = "";
        }

        return dots;
    }

    public static String getUnderscore()
    {
        if (!undTimer.passed(500))
        {
            return "_";
        }
        else
        {
            if (undTimer.passed(1000))
            {
                undTimer.reset();
            }

            return "";
        }
    }

}
