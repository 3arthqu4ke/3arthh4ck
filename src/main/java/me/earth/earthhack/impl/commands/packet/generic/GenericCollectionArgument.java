package me.earth.earthhack.impl.commands.packet.generic;

import com.google.common.collect.Lists;
import me.earth.earthhack.impl.commands.packet.PacketArgument;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class GenericCollectionArgument<T>
        extends AbstractIterableArgument<T, Collection<T>>
{
    public GenericCollectionArgument(Constructor<?> ctr,
                                     int argIndex,
                                     PacketArgument<T> parser)
    {
        super(Iterable.class, ctr, argIndex, parser);
    }

    @Override
    protected Collection<T> create(T[] array)
    {
        return Lists.newArrayList(array);
    }

}
