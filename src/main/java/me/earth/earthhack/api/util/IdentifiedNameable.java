package me.earth.earthhack.api.util;

import me.earth.earthhack.api.util.interfaces.Nameable;

public class IdentifiedNameable implements Nameable
{
    private final String lower;
    private final String name;

    public IdentifiedNameable(String name)
    {
        this.lower = name.toLowerCase();
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        return lower.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof IdentifiedNameable)
        {
            return ((IdentifiedNameable) o).lower.equals(this.lower);
        }

        return false;
    }

}
