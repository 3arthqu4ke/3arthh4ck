package me.earth.earthhack.api.util;

public class TextUtil
{
    /**
     * @param string the string that should be tested.
     * @param prefix the prefix the first string should start with.
     * @return {@link String#startsWith(String)}, but ignoring case.
     */
    public static boolean startsWith(String string, String prefix)
    {
        if (string == null || prefix == null)
        {
            return false;
        }

        return string.toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * Uses the {@link String#substring(int)} method with
     * the given String, but safely: if the given String is null
     * or the beginIndex is >= to the strings length an empty String
     * will be returned. If the beginIndex is < 0 it will be clamped
     * to 0.
     *
     * @param string the string to get the substring from.
     * @param beginIndex the beginning index, inclusive.
     * @return the specified substring.
     */
    public static String substring(String string, int beginIndex)
    {
        if (string != null && beginIndex <= string.length())
        {
            return string.substring(Math.max(beginIndex, 0));
        }

        return "";
    }

    /**
     * Uses the {@link String#substring(int)} method with
     * the given String, but safely: if the given String is null,
     * the endIndex is <= 0, the endIndex is >= the beginIndex
     * or the beginIndex is >= to the strings length an empty String
     * will be returned. If the beginIndex is < 0 it will be clamped
     * to 0. If the endIndex is bigger than the strings length it will
     * be clamped to that value.
     *
     * @param string the string to get the substring from.
     * @param beginIndex the beginning index, inclusive.
     * @param endIndex the ending index, exclusive.
     * @return the specified substring.
     */
    public static String substring(String string, int beginIndex, int endIndex)
    {
        if (string != null
                && beginIndex <= string.length()
                && endIndex > 0
                && endIndex >= beginIndex)
        {
            return string.substring(
                    Math.max(0, beginIndex),
                    Math.min(endIndex, string.length()));
        }

        return "";
    }

    /**
     * Returns the Integers value as a full 32bit hex string:
     * <p>
     * <p>get32BitString(-1) -> "FFFFFFFF"
     * <p>get32BitString(0) -> "00000000"
     * <p>get32BitString(128) -> "00000080"
     * <p>...
     *
     * @param value the integer to get the 32bit string from.
     * @return a 32bit string representing the integers value.
     */
    public static String get32BitString(int value)
    {
        StringBuilder r = new StringBuilder(Integer.toHexString(value));

        while (r.length() < 8)
        {
            r.insert(0, 0);
        }

        return r.toString().toUpperCase();
    }

    /**
     * This operation uses the {@link String#substring(int, int)}
     * method for the values (0, 1) and the {@link String#substring(int)}
     * method for the value 1 on the given String. So check that these
     * methods can be used without throwing an exception before using this
     * function.
     *
     * @param str the String to capitalize.
     * @return the string with its first letter turned uppercase.
     */
    public static String capitalize(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
