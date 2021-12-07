package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;

public interface IPacketHandler
{
    void handle(IConnection connection, byte[] bytes) throws IOException;

}
