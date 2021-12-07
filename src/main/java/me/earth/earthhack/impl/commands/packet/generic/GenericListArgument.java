package me.earth.earthhack.impl.commands.packet.generic;

import com.google.common.collect.Lists;
import me.earth.earthhack.impl.commands.packet.PacketArgument;

import java.lang.reflect.Constructor;
import java.util.List;

public class GenericListArgument<T>
        extends AbstractIterableArgument<T, List<T>>
{
    public GenericListArgument(Constructor<?> ctr,
                               int argIndex,
                               PacketArgument<T> parser)
    {
        super(Iterable.class, ctr, argIndex, parser);
    }

    @Override
    protected List<T> create(T[] array)
    {
        return Lists.newArrayList(array);
    }

}
