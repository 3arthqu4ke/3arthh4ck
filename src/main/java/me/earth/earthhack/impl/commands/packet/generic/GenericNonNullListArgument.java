package me.earth.earthhack.impl.commands.packet.generic;

import me.earth.earthhack.impl.commands.packet.PacketArgument;
import net.minecraft.util.NonNullList;

import java.lang.reflect.Constructor;

public class GenericNonNullListArgument<T>
        extends AbstractIterableArgument<T, NonNullList<T>>
{
    public GenericNonNullListArgument(Constructor<?> ctr,
                                      int argIndex,
                                      PacketArgument<T> parser)
    {
        super(Iterable.class, ctr, argIndex, parser);
    }

    @Override
    protected NonNullList<T> create(T[] array)
    {
        NonNullList<T> list = NonNullList.create();
        for (T t : array)
        {
            if (t != null)
            {
                list.add(t);
            }
        }

        return list;
    }

}
