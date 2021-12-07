package me.earth.earthhack.impl.commands.packet.array;

import me.earth.earthhack.impl.commands.packet.PacketArgument;

import java.util.function.Function;

public class FunctionArrayArgument<T> extends AbstractArrayArgument<T>
{
    private final Function<Integer, T[]> function;

    public FunctionArrayArgument(Class<T[]> type,
                                 PacketArgument<T> parser,
                                 Function<Integer, T[]> function)
    {
        super(type, parser);
        this.function = function;
    }

    @Override
    protected T[] create(int size)
    {
        return function.apply(size);
    }

}
