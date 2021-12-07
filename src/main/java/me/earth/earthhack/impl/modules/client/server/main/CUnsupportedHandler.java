package me.earth.earthhack.impl.modules.client.server.main;

import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.ILogger;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;

import java.io.IOException;

/**
 * Handler for packets unsupported by the client.
 */
public class CUnsupportedHandler implements IPacketHandler
{
    private final ILogger logger;
    private final int id;

    public CUnsupportedHandler(ILogger logger, int id)
    {
        this.logger  = logger;
        this.id      = id;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException
    {
        logger.log("Received packet with unsupported id: " + id);
    }

}
