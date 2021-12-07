package me.earth.earthhack.impl.commands.packet.array;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.arguments.IntArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class IntArrayArgument extends AbstractArgument<int[]>
{
    private static final PacketArgument<Integer> PARSER = new IntArgument();

    public IntArrayArgument()
    {
        super(int[].class);
    }

    @Override
    public int[] fromString(String argument) throws ArgParseException
    {
        String[] split = argument.split("]");
        int[] array = new int[split.length];
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
            return PossibleInputs.empty().setRest("<Integer]Integer...>");
        }

        return PossibleInputs.empty();
    }

}
