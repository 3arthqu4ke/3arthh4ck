package me.earth.earthhack.api.util.bind;

import me.earth.earthhack.pingbypass.input.Keyboard;

/**
 * Represents binds for the client. A key of -1 means no bind.
 */
public class Bind
{
    private final int key;

    private Bind(int key)
    {
        this.key = key;
    }

    /** @return the key for this bind (-1 if none). */
    public int getKey()
    {
        return key;
    }

    @Override
    public int hashCode()
    {
        return key;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Bind)
        {
            return ((Bind) o).key == this.key;
        }

        return false;
    }

    @Override
    public String toString()
    {
        return key < 0 ? "NONE" : Keyboard.getKeyName(key);
    }

    /** @return a bind with key == -1. */
    public static Bind none()
    {
        return new Bind(-1);
    }

    /** @return a bind with key == the given key. */
    public static Bind fromKey(int key)
    {
        return new Bind(key);
    }

    /** Uses {@link Keyboard#getKeyIndex(java.lang.String)} to parse. */
    public static Bind fromString(String stringIn)
    {
        String string = stringIn.toUpperCase();
        if (string.equals("NONE")) // NONE exists and is 0... maybe fix?
        {
            return none();
        }

        return new Bind(Keyboard.getKeyIndex(string));
    }

}
