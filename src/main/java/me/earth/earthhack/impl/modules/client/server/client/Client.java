package me.earth.earthhack.impl.modules.client.server.client;

import me.earth.earthhack.impl.modules.client.server.api.*;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;

import java.io.DataInputStream;
import java.io.IOException;

public final class Client extends AbstractConnection
        implements SafeRunnable, IClient
{
    private final IPacketManager manager;
    private final IServerList serverList;

    public Client(IPacketManager manager, IServerList serverList, String ip, int port)
            throws IOException
    {
        super(ip, port);
        this.manager = manager;
        this.serverList = serverList;
    }

    @Override
    public void runSafely() throws Throwable
    {
        try (DataInputStream in = new DataInputStream(getInputStream()))
        {
            while (isOpen())
            {
                IPacket packet = ProtocolUtil.readPacket(in);
                manager.handle(this, packet.getId(), packet.getBuffer());
            }
        }
    }

    @Override
    public void handle(Throwable t)
    {
        t.printStackTrace();
        close();
    }

    @Override
    public IServerList getServerList()
    {
        return serverList;
    }

}
