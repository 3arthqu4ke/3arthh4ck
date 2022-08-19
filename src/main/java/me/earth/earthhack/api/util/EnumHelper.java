package me.earth.earthhack.api.util;

/**
 * Utility for {@link Enum}s.
 */
public class EnumHelper {
    /**
     * @param entry the Enum value to get the next one from.
     * @return the next enum value in the enum,
     * or the first one if the given
     * one is the last value in the enum.
     */
    public static Enum<?> next(Enum<?> entry) {
        Enum<?>[] array = entry.getDeclaringClass().getEnumConstants();
        return array.length - 1 == entry.ordinal()
                ? array[0]
                : array[entry.ordinal() + 1];
    }

    /**
     * @param entry the Enum value to get the previous one from.
     * @return the previous enum value in the enum,
     * or the last one if the given
     * one is the first value in the enum.
     */
    public static Enum<?> previous(Enum<?> entry) {
        Enum<?>[] array = entry.getDeclaringClass().getEnumConstants();
        return entry.ordinal() - 1 < 0 ? array[array.length - 1] : array[entry.ordinal() - 1];
    }


    /**
     * Parses the EnumValue for the Enum from String.
     *
     * @param initial the initial enum value.
     * @param name    the name to parse (ignoreCase).
     * @return initial if not able to parse or the enum value for the string.
     */
    public static Enum<?> fromString(Enum<?> initial, String name)
    {
        Enum<?> e = fromString(initial.getDeclaringClass(), name);
        if (e != null)
        {
            return e;
        }

        return initial;
    }

    /**
     * Parses the EnumValue for the Enum from String.
     *
     * @param type the type of the enum
     * @param name the name to parse (ignoreCase).
     * @return initial if not able to parse or the enum value for the string.
     */
    public static <T extends Enum<?>> T fromString(Class<T> type, String name)
    {
        for (T constant : type.getEnumConstants())
        {
            if (constant.name().equalsIgnoreCase(name))
            {
                return constant;
            }
        }

        return null;
    }

    public static Enum<?> getEnumStartingWith(String prefixIn,
                                              Class<? extends Enum<?>> type)
    {
        if (prefixIn == null)
        {
            return null;
        }

        String prefix = prefixIn.toLowerCase();
        Enum<?>[] array = type.getEnumConstants();
        for (Enum<?> entry : array)
        {
            if (entry.name().toLowerCase().startsWith(prefix))
            {
                return entry;
            }
        }

        return null;
    }

}
