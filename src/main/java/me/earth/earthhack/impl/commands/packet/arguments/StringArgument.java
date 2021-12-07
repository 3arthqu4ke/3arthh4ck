package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class StringArgument extends AbstractArgument<String>
{
    public StringArgument()
    {
        super(String.class);
    }

    @Override
    public String fromString(String argument) throws ArgParseException
    {
        return argument;
    }

}
