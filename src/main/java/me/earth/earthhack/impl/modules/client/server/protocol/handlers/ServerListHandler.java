package me.earth.earthhack.impl.modules.client.server.protocol.handlers;

import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionEntry;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;
import me.earth.earthhack.impl.modules.client.server.api.IServerList;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;

import java.io.IOException;

public class ServerListHandler implements IPacketHandler
{
    private final IServerList serverList;

    public ServerListHandler(IServerList serverList)
    {
        this.serverList = serverList;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException
    {
        IConnectionEntry[] list = ProtocolUtil.deserializeServerList(bytes);
        serverList.set(list);
    }

}
