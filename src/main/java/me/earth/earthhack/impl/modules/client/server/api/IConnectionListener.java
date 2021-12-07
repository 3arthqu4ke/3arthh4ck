package me.earth.earthhack.impl.modules.client.server.api;

public interface IConnectionListener
{
    void onJoin(IConnectionManager manager, IConnection connection);

    void onLeave(IConnectionManager manager, IConnection connection);

}
