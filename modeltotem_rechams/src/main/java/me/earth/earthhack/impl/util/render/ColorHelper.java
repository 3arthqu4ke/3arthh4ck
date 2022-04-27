package me.earth.earthhack.impl.util.render;

import java.awt.*;

public class ColorHelper
{
    public static float getFactor(float red, float green, float blue)
    {
        if (blue < 0.0f)
        {
            ++blue;
        }

        if (blue > 1.0f)
        {
            --blue;
        }

        if (6.0f * blue < 1.0f)
        {
            return red + (green - red) * 6.0f * blue;
        }
        else if (2.0f * blue < 1.0f)
        {
            return green;
        }
        else
        {
            return 3.0F * blue < 2.0f
                    ? red + (green - red) * 6.0f * (0.6666667f - blue)
                    : red;
        }
    }

    public static Color toColor(float red, float green, float blue, float alpha)
    {
        if (!(green < 0.0f) && !(green > 100.0f))
        {
            if (!(blue < 0.0f) && !(blue > 100.0f))
            {
                if (!(alpha < 0.0f) && !(alpha > 1.0f))
                {
                    red = red % 360.0f / 360.0f;
                    green /= 100.0f;
                    blue /= 100.0f;

                    float blueOff;
                    if (blue < 0.0)
                    {
                        blueOff = blue * (1.0f + green);
                    }
                    else
                    {
                        blueOff = blue + green - green * blue;
                    }

                    green = 2.0f * blue - blueOff;
                    blue = Math.max(0.0f, getFactor(green, blueOff, red + 0.33333334f));
                    float max = Math.max(0.0f, getFactor(green, blueOff, red));
                    green = Math.max(0.0f, getFactor(green, blueOff, red - 0.33333334f));
                    blue = Math.min(blue, 1.0f);
                    max = Math.min(max, 1.0f);
                    green = Math.min(green, 1.0f);
                    return new Color(blue, max, green, alpha);
                }
                else
                {
                    throw new IllegalArgumentException(
                        "Color parameter outside of expected range - Alpha");
                }
            }
            else
            {
                throw new IllegalArgumentException(
                    "Color parameter outside of expected range - Lightness");
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "Color parameter outside of expected range - Saturation");
        }
    }

}
