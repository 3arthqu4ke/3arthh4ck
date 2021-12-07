package me.earth.earthhack.impl.modules.client.server.protocol.handlers;

import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.ILogger;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class MessageHandler implements IPacketHandler
{
    private final Function<String, String> format;
    private final ILogger logger;

    public MessageHandler(ILogger logger)
    {
        this(logger, null);
    }

    public MessageHandler(ILogger logger, Function<String, String> format)
    {
        this.logger = logger;
        this.format = format;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException
    {
        String message = new String(bytes, StandardCharsets.UTF_8);
        if (format != null)
        {
            logger.log(format.apply(message));
        }
        else
        {
            logger.log(new String(bytes, StandardCharsets.UTF_8));
        }
    }

}
