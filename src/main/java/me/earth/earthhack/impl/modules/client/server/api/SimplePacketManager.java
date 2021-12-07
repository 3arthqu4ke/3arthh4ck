package me.earth.earthhack.impl.modules.client.server.api;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimplePacketManager implements IPacketManager
{
    private final Map<Integer, IPacketHandler> handlers;

    public SimplePacketManager()
    {
        this.handlers = new ConcurrentHashMap<>();
    }

    @Override
    public void handle(IConnection connection, int id, byte[] bytes)
            throws UnknownProtocolException, IOException
    {
        IPacketHandler handler = handlers.get(id);
        if (handler == null)
        {
            throw new UnknownProtocolException(id);
        }

        handler.handle(connection, bytes);
    }

    @Override
    public void add(int id, IPacketHandler handler)
    {
        handlers.put(id, handler);
    }

    @Override
    public IPacketHandler getHandlerFor(int id)
    {
        return handlers.get(id);
    }

}
