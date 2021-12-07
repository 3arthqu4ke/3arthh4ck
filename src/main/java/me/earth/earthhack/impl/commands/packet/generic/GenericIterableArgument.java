package me.earth.earthhack.impl.commands.packet.generic;

import com.google.common.collect.Lists;
import me.earth.earthhack.impl.commands.packet.PacketArgument;

import java.lang.reflect.Constructor;

public class GenericIterableArgument<T>
        extends AbstractIterableArgument<T, Iterable<T>>
{
    public GenericIterableArgument(Constructor<?> ctr,
                                   int argIndex,
                                   PacketArgument<T> parser)
    {
        super(Iterable.class, ctr, argIndex, parser);
    }

    @Override
    protected Iterable<T> create(T[] array)
    {
        return Lists.newArrayList(array);
    }

}
