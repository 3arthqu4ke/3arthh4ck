package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class DoubleArgument extends AbstractArgument<Double>
{
    public DoubleArgument()
    {
        super(Double.class);
    }

    @Override
    public Double fromString(String argument) throws ArgParseException
    {
        try
        {
            return Double.parseDouble(argument);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Could not parse " + argument + " to double!");
        }
    }

}
