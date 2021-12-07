package me.earth.earthhack.impl.modules.client.server.api;

import java.util.List;

public interface IConnectionManager extends ISender
{
    IPacketManager getHandler();

    boolean accept(IConnection client);

    void remove(IConnection connection);

    List<IConnection> getConnections();

    void addListener(IConnectionListener listener);

    void removeListener(IConnectionListener listener);

}
