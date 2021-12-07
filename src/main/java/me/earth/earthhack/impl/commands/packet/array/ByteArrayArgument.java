package me.earth.earthhack.impl.commands.packet.array;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.arguments.ByteArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;

public class ByteArrayArgument extends AbstractArgument<byte[]>
{
    private static final PacketArgument<Byte> PARSER = new ByteArgument();

    public ByteArrayArgument()
    {
        super(byte[].class);
    }

    @Override
    public byte[] fromString(String argument) throws ArgParseException
    {
        String[] split = argument.split("]");
        byte[] array = new byte[split.length];
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
            return PossibleInputs.empty().setRest("<Byte]Byte...>");
        }

        return PossibleInputs.empty();
    }

}
