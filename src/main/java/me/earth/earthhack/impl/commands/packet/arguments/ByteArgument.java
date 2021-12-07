package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class ByteArgument extends AbstractArgument<Byte>
{
    public ByteArgument()
    {
        super(Byte.class);
    }

    @Override
    public Byte fromString(String argument) throws ArgParseException
    {
        try
        {
            return (byte) Long.parseLong(argument);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Could not parse " + argument + " to byte!");
        }
    }

}
