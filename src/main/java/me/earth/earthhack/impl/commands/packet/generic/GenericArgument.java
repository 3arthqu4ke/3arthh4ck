package me.earth.earthhack.impl.commands.packet.generic;

import me.earth.earthhack.impl.commands.packet.AbstractArgument;

import java.lang.reflect.Constructor;

public abstract class GenericArgument<T> extends AbstractArgument<T>
{
    private final Constructor<?> ctr;
    private final int argIndex;

    public GenericArgument(Class<? super T> type,
                           Constructor<?> ctr,
                           int argIndex)
    {
        super(type);
        this.ctr = ctr;
        this.argIndex = argIndex;
    }

    public Constructor<?> getConstructor()
    {
        return ctr;
    }

    public int getArgIndex()
    {
        return argIndex;
    }

}
