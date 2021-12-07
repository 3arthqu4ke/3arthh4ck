package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class FloatArgument extends AbstractArgument<Float>
{
    public FloatArgument()
    {
        super(Float.class);
    }

    @Override
    public Float fromString(String argument) throws ArgParseException
    {
        try
        {
            return (float) Double.parseDouble(argument);
        }
        catch (Exception e)
        {
            throw new ArgParseException(
                    "Could not parse " + argument + " to float!");
        }
    }

}
