package me.earth.earthhack.impl.modules.client.server.host;

import me.earth.earthhack.impl.modules.client.server.api.AbstractConnection;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;
import me.earth.earthhack.impl.modules.client.server.api.IPacket;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public final class Connection extends AbstractConnection implements SafeRunnable
{
    private static final AtomicInteger ID = new AtomicInteger();

    private final IConnectionManager manager;
    private final int id;

    public Connection(IConnectionManager manager, Socket socket)
    {
        super(socket);
        this.manager = manager;
        this.name    = "unknown";
        this.id      = ID.incrementAndGet();
    }

    @Override
    public void runSafely() throws Throwable
    {
        try (DataInputStream in = new DataInputStream(getInputStream()))
        {
            while (isOpen())
            {
                IPacket p = ProtocolUtil.readPacket(in);
                manager.getHandler().handle(this, p.getId(), p.getBuffer());
            }
        }
    }

    @Override
    public void handle(Throwable t)
    {
        manager.remove(this);
    }

    @Override
    public String getName()
    {
        return name == null ? id + "" : name;
    }

    @Override
    public int getId()
    {
        return id;
    }

}
