package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;

public interface IPacketManager
{
    void handle(IConnection connection, int id, byte[] bytes)
            throws UnknownProtocolException, IOException;

    void add(int id, IPacketHandler handler);

    IPacketHandler getHandlerFor(int id);

}
