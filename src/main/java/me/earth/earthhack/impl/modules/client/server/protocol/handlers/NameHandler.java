package me.earth.earthhack.impl.modules.client.server.protocol.handlers;

import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.ILogger;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;

import java.nio.charset.StandardCharsets;

public class NameHandler implements IPacketHandler
{
    private final ILogger logger;

    public NameHandler(ILogger logger)
    {
        this.logger = logger;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes)
    {
        String name = new String(bytes, StandardCharsets.UTF_8);

        logger.log("Connection: "
                    + connection.getId()
                    + " previously ("
                    + connection.getName()
                    + ") set it's name to: "
                    + name
                    + ".");

        connection.setName(name);
    }

}
