package me.earth.earthhack.impl.commands.packet.array;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

public abstract class AbstractArrayArgument<T> extends AbstractArgument<T[]>
{
    protected final PacketArgument<T> parser;

    public AbstractArrayArgument(Class<T[]> type, PacketArgument<T> parser)
    {
        super(type);
        this.parser = parser;
    }

    protected abstract T[] create(int size);

    @Override
    public T[] fromString(String argument) throws ArgParseException
    {
        String[] split = argument.split("]");
        T[] array = create(split.length);
        for (int i = 0; i < split.length; i++)
        {
            array[i] = parser.fromString(split[i]);
        }

        return array;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        if (argument == null || argument.isEmpty())
        {
            return PossibleInputs.empty().setRest("<"
                    + parser.getPossibleInputs(null).getRest() + "]"
                    + parser.getPossibleInputs(null).getRest() + "]...>");
        }

        String[] split = argument.split("]");
        return parser.getPossibleInputs(split[split.length - 1]);
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer)
    {
        return CustomCompleterResult.PASS;
    }

}
