package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleConnectionManager implements IConnectionManager
{
    private final IPacketManager packetManager;
    private final List<IConnectionListener> listeners;
    private final List<IConnection> connections;
    private final int maxConnections;

    public SimpleConnectionManager(IPacketManager packetManager,
                                   int maxConnections)
    {
        this.packetManager  = packetManager;
        this.maxConnections = maxConnections;
        this.connections    = new CopyOnWriteArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public IPacketManager getHandler()
    {
        return packetManager;
    }

    @Override
    public boolean accept(IConnection client)
    {
        if (connections.size() >= maxConnections)
        {
            return false;
        }

        connections.add(client);
        for (IConnectionListener listener : listeners)
        {
            if (listener != null)
            {
                listener.onJoin(this, client);
            }
        }
        return true;
    }

    @Override
    public void remove(IConnection connection)
    {
        if (connection.isOpen())
        {
            connection.close();
        }

        connections.remove(connection);
        for (IConnectionListener listener : listeners)
        {
            if (listener != null)
            {
                listener.onLeave(this, connection);
            }
        }
    }

    @Override
    public List<IConnection> getConnections()
    {
        return connections;
    }

    @Override
    public void addListener(IConnectionListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IConnectionListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void send(byte[] packet) throws IOException
    {
        for (IConnection connection : connections)
        {
            try
            {
                connection.send(packet);
            }
            catch (IOException e)
            {
                // TODO: log connection id.
                remove(connection);
                e.printStackTrace();
                // throw e;
            }
        }
    }

}
