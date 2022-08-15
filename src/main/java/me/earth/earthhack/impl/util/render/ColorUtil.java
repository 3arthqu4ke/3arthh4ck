package me.earth.earthhack.impl.util.render;

import java.awt.*;

public class ColorUtil {
    public static int toARGB(int r, int g, int b) {
        return toARGB(r, g, b, 255);
    }

    public static int toARGB(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + (b) + (a << 24);
    }

    public static int toARGB(Color color) {
        return toARGB(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static Color fromARGB(int color) {
        int[] bArray = fromARGBtoRGBAArray(color);
        return new Color(bArray[0], bArray[1], bArray[2], bArray[3]);
    }

    public static float[] toArray(Color color) {
        return new float[]
                {
                        color.getRed() / 255.0F,
                        color.getGreen() / 255.0F,
                        color.getBlue() / 255.0F,
                        color.getAlpha() / 255.0F
                };
    }

    public static float[] toArray(int color) {
        return new float[]
                {
                        (color >> 16 & 255) / 255.0F,
                        (color >> 8 & 255) / 255.0F,
                        (color & 255) / 255.0F,
                        (color >> 24 & 255) / 255.0F,
                };
    }

    public static int[] fromARGBtoRGBAArray(int color) {
        return new int[]
            {
                color >> 16 & 255,
                color >> 8 & 255,
                color & 255,
                color >> 24 & 255,
            };
    }

    public static int staticRainbow(float offset, Color color) {
        double timer = System.currentTimeMillis() % 1750.0 / 850.0;
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = (float) (hsb[2] * Math.abs((offset + timer) % 1f - 0.55f) + 0.45f);
        return Color.HSBtoRGB(hsb[0], hsb[1], brightness);
    }

    public static Color getRainbow(int speed, int offset, float s, float brightness) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, s, brightness);
    }
}
