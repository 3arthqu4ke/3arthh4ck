package me.earth.earthhack.impl.modules.client.server.api;

public interface IServerList
{
    IConnectionEntry[] get();

    void set(IConnectionEntry[] entries);

}
