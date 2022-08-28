package me.earth.earthhack.impl.util.math;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtil {
    public static int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : (Math.min(num, max));
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : (Math.min(num, max));
    }

    public static long clamp(long num, long min, long max) {
        return num < min ? min : (Math.min(num, max));
    }

    public static BigDecimal clamp(BigDecimal num,
                                   BigDecimal min,
                                   BigDecimal max) {
        return smallerThan(num, min) ? min : biggerThan(num, max) ? max : num;
    }

    /**
     * @param bigger check if this BigDecimal is bigger
     * @param than   this one.
     * @return <tt>true</tt> if {@link BigDecimal#compareTo(BigDecimal)} > 0
     * for <code>bigger.compareTo(than)</code>
     */
    public static boolean biggerThan(BigDecimal bigger, BigDecimal than) {
        return bigger.compareTo(than) > 0;
    }

    /**
     * @param bd1 check if this BigDecimal's value is equal to
     * @param bd2 this one's.
     * @return <tt>true</tt> if {@link BigDecimal#compareTo(BigDecimal)} == 0
     * for <code>bd1.compareTo(bd1)</code>
     */
    public static boolean equal(BigDecimal bd1, BigDecimal bd2) {
        return bd1.compareTo(bd2) == 0;
    }

    /**
     * @param smaller check if this BigDecimal is smaller
     * @param than    this one.
     * @return <tt>true</tt> if {@link BigDecimal#compareTo(BigDecimal)} < 0
     * for <code>smaller.compareTo(than)</code>
     */
    public static boolean smallerThan(BigDecimal smaller, BigDecimal than) {
        return smaller.compareTo(than) < 0;
    }

    public static long squareToLong(int i)
    {
        return (long) i * i;
    }

    public static int square(int i)
    {
        return i * i;
    }

    public static float square(float i)
    {
        return i * i;
    }

    public static double square(double i)
    {
        return i * i;
    }

    /**
     * A simple to Math.pow operation, taking
     * integers as power.
     *
     * @param number the number to exponentiate.
     * @param power  the power.
     * @return number to the power of the power.
     */
    public static double simplePow(double number, int power) {
        if (power == 0) {
            return 1;
        } else if (power < 0) {
            return 1 / simplePow(number, power * -1);
        }

        double result = number;
        for (int i = 1; i < power; i++) {
            result *= number;
        }

        return result;
    }

    public static double round(double value, int places) {
        return places < 0 ? value : (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public static float round(float value, int places) {
        return places < 0 ? value : (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP).floatValue();
    }

    public static float round(float value, int places, float min, float max) {
        return MathHelper.clamp(places < 0 ? value : (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP).floatValue(), min, max);
    }

    public static float rad(float angle) {
        return (float) (angle * Math.PI / 180);
    }

    public static double degree(double angle) {
        return angle / Math.PI * 180;
    }

    public static double angle(Vec3d vec3d, Vec3d other) {
        double lengthSq = vec3d.length() * other.length();

        if (lengthSq < 1.0E-4D) {
            return 0.0;
        }

        double dot = vec3d.dotProduct(other);
        double arg = dot / lengthSq;

        if (arg > 1) {
            return 0.0;
        } else if (arg < -1) {
            return 180.0;
        }

        return Math.acos(arg) * 180.0f / Math.PI;
    }

    /**
     * Draws a Vec3d between 2 Vec3ds, from first
     * parameter to second.
     *
     * @param from the start vec3d.
     * @param to   the end vec3d.
     * @return return a vec3d looking from "from" to "to".
     */
    public static Vec3d fromTo(Vec3d from, Vec3d to) {
        return fromTo(from.x, from.y, from.z, to);
    }

    /**
     * Convenience method.
     */
    public static Vec3d fromTo(Vec3d from, double x, double y, double z) {
        return fromTo(from.x, from.y, from.z, x, y, z);
    }

    /**
     * Convenience method.
     */
    public static Vec3d fromTo(double x, double y, double z, Vec3d to) {
        return fromTo(x, y, z, to.x, to.y, to.z);
    }

    /**
     * Convenience method.
     */
    public static Vec3d fromTo(double x, double y, double z, double x2, double y2, double z2) {
        return new Vec3d(x2 - x, y2 - y, z2 - z);
    }

    public static double distance2D(Vec3d from, Vec3d to) {
        double x = to.x - from.x;
        double z = to.z - from.z;
        return Math.sqrt(x * x + z * z);
    }

    /**
     * Gets the 2 facings that stand in a 90 degree angle
     * on the given facing. That means for NORTH or SOUTH
     * an array of WEST and EAST is returned and the other
     * way around. For <tt>null</tt>, UP or DOWN an array
     * of UP and DOWN is returned.
     *
     * @param facing the facing to get 2 facings for.
     * @return the 2 facings "adjacent" to the given one.
     */
    public static EnumFacing[] getRotated(EnumFacing facing) {
        switch (facing) {
            case DOWN:
            case UP:
                break;
            case NORTH:
            case SOUTH:
                return new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST};
            case WEST:
            case EAST:
                return new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH};
        }

        return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
    }

    /**
     * Gets random number in the range
     * of the min param and max param
     *
     * @param min the min number
     * @param max the max number
     * @return random number between min and max
     */
    public static int getRandomInRange(int min, int max) {
        return min + new Random().nextInt(max - min);
    }

    public static double getRandomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    public static float getRandomInRange(float min, float max) {
        Random random = new Random();
        float range = max - min;
        float scaled = random.nextFloat() * range;
        if (scaled > max) {
            scaled = max;
        }
        float shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    // TODO: inline
    public static int swapEndianness(int value)
    {
        int leftmostBytes;
        int leftMiddleBytes;
        int rightMiddleBytes;
        int rightmostBytes;
        leftmostBytes = (value & 0x000000FF);
        leftMiddleBytes = (value & 0x0000FF00) >> 8;
        rightMiddleBytes = (value & 0x00FF0000) >> 16;
        rightmostBytes = (value & 0xFF000000) >> 24;
        leftmostBytes <<= 24;
        leftMiddleBytes <<= 16;
        rightMiddleBytes <<= 8;
        return (leftmostBytes | leftMiddleBytes | rightMiddleBytes | rightmostBytes);
    }

    public static int swapRGBEndianness(int value)
    {
        int leftmostBytes;
        int middleBytes;
        int rightmostBytes;
        leftmostBytes = (value & 0x0000FF);
        middleBytes = (value & 0x00FF00) >> 8;
        rightmostBytes = (value & 0xFF0000) >> 16;
        leftmostBytes <<= 24;
        middleBytes <<= 16;
        rightmostBytes <<= 8;
        return (leftmostBytes | middleBytes | rightmostBytes);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toRGB(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }

    public static int toRGBAReversed(int r, int g, int b) {
        return (b << 16) + (g << 8) + r;
    }

    public static int[] toRGBAArray(int colorBuffer) {
        return new int[]{colorBuffer >> 16 & 255, colorBuffer >> 8 & 255, colorBuffer & 255}; // THIS IS B,G,R FOR THE PURPOSE OF USE IN MIXINBUFFERBUILDER
    }

    public static Vector3f mix(Vector3f first, Vector3f second, float factor)
    {
        return new Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor);
    }

    public static Vector3f lerp(Vector3f start, Vector3f end, float progression)
    {
        float x = start.x + (end.x - start.x) * progression;
        float y = start.y + (end.y - start.y) * progression;
        float z = start.z + (end.z - start.z) * progression;
        return new Vector3f(x, y, z);
    }

}
