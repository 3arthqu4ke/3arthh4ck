package me.earth.earthhack.impl.util.misc;

public class Wrapper<T>
{
    protected final T value;

    public Wrapper(T value)
    {
        this.value = value;
    }

    public T get()
    {
        return value;
    }

}
