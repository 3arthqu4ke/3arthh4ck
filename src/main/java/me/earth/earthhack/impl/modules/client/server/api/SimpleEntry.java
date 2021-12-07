package me.earth.earthhack.impl.modules.client.server.api;

public class SimpleEntry implements IConnectionEntry
{
    private final String name;
    private final int id;

    public SimpleEntry(String name, int id)
    {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getId()
    {
        return id;
    }

}
