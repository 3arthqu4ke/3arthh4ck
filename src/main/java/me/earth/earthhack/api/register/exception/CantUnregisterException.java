package me.earth.earthhack.api.register.exception;

import me.earth.earthhack.api.util.interfaces.Nameable;

public class CantUnregisterException extends Exception
{
    private final Nameable nameable;

    public CantUnregisterException(Nameable nameable)
    {
        this("Can't unregister "
                + (nameable == null ? "null" : nameable.getName())
                + ".",
            nameable);
    }

    public CantUnregisterException(String message, Nameable nameable)
    {
        super(message);
        this.nameable = nameable;
    }

    public Nameable getObject()
    {
        return nameable;
    }

}
