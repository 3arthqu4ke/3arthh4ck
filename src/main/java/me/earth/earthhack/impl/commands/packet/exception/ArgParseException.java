package me.earth.earthhack.impl.commands.packet.exception;

import me.earth.earthhack.impl.util.exception.NoStackTraceException;

// TODO: Color all messages nicely
public class ArgParseException extends NoStackTraceException
{
    public ArgParseException(String message)
    {
        super(message);
    }

    public static long tryLong(String string, String message)
            throws ArgParseException
    {
        try
        {
            return Long.parseLong(string);
        }
        catch (NumberFormatException e)
        {
            throw new ArgParseException(
                    "Couldn't parse " + message + ": " + string + "!");
        }
    }

    public static double tryDouble(String string, String message)
            throws ArgParseException
    {
        try
        {
            return Double.parseDouble(string);
        }
        catch (NumberFormatException e)
        {
            throw new ArgParseException(
                    "Couldn't parse " + message + ": " + string + "!");
        }
    }

}
