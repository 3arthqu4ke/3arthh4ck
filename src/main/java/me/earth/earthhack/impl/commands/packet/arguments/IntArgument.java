package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class IntArgument extends AbstractArgument<Integer>
{
    public IntArgument()
    {
        super(Integer.class);
    }

    @Override
    public Integer fromString(String argument) throws ArgParseException
    {
        try
        {
            return (int) Long.parseLong(argument);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Could not parse " + argument + " to Integer!");
        }
    }

}
