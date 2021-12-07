package me.earth.earthhack.impl.commands.packet.generic;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.array.SimpleArrayArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;

import java.lang.reflect.Constructor;

public abstract class AbstractIterableArgument<T, S extends Iterable<T>>
        extends GenericArgument<S>
{
    private final PacketArgument<T> parser;

    public AbstractIterableArgument(Class<? super S> type,
                                    Constructor<?> ctr,
                                    int argIndex,
                                    PacketArgument<T> parser)
    {
        super(type, ctr, argIndex);
        this.parser = parser;
    }

    protected abstract S create(T[] array);

    @Override
    public S fromString(String argument)
            throws ArgParseException
    {
        T[] array = SimpleArrayArgument.toArray(argument, parser);
        return create(array);
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        if (argument == null || argument.isEmpty())
        {
            return PossibleInputs.empty()
                    .setRest("<"
                            + parser.getPossibleInputs(null).getRest() + "]"
                        + parser.getPossibleInputs(null).getRest() + "...>");
        }

        String[] split = argument.split("]");
        return parser.getPossibleInputs(split[split.length - 1]);
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer)
    {
        return super.onTabComplete(completer);
    }

}
