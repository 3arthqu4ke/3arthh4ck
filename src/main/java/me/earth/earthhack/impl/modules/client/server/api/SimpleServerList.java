package me.earth.earthhack.impl.modules.client.server.api;

public class SimpleServerList implements IServerList
{
    private IConnectionEntry[] entries = new IConnectionEntry[0];

    @Override
    public IConnectionEntry[] get()
    {
        return entries;
    }

    @Override
    public void set(IConnectionEntry[] entries)
    {
        this.entries = entries;
    }

}
