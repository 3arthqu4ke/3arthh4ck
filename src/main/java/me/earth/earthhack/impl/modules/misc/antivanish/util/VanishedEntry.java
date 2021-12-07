package me.earth.earthhack.impl.modules.misc.antivanish.util;

public class VanishedEntry
{
    private final String name;
    private final long time;

    public VanishedEntry(String name)
    {
        this.name = name;
        this.time = System.currentTimeMillis();
    }

    public String getName()
    {
        return name;
    }

    public long getTime()
    {
        return time;
    }

}
