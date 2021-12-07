package me.earth.earthhack.impl.modules.client.server.api;

public interface IHost extends ICloseable
{
    IConnectionManager getConnectionManager();

    int getPort();

}