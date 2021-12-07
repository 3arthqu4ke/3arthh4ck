package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class LongArgument extends AbstractArgument<Long>
{
    public LongArgument()
    {
        super(Long.class);
    }

    @Override
    public Long fromString(String argument) throws ArgParseException
    {
        try
        {
            return Long.parseLong(argument);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Could not parse " + argument + " to Long!");
        }
    }

}
