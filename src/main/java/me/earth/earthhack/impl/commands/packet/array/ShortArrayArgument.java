package me.earth.earthhack.impl.commands.packet.array;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ShortArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class ShortArrayArgument extends AbstractArgument<short[]>
{
    private static final PacketArgument<Short> PARSER = new ShortArgument();

    public ShortArrayArgument()
    {
        super(short[].class);
    }

    @Override
    public short[] fromString(String argument) throws ArgParseException
    {
        String[] split = argument.split("]");
        short[] array = new short[split.length];
        for (int i = 0; i < split.length; i++)
        {
            array[i] = PARSER.fromString(split[i]);
        }

        return array;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        if (argument == null || argument.isEmpty())
        {
            return PossibleInputs.empty().setRest("<Short]Short...>");
        }

        return PossibleInputs.empty();
    }

}
