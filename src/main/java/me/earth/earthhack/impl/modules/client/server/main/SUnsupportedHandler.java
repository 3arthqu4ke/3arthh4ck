package me.earth.earthhack.impl.modules.client.server.main;

import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.Protocol;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;

import java.io.IOException;

/**
 * Handler for Packets that are unsupported by the server.
 */
public class SUnsupportedHandler implements IPacketHandler
{
    private final String message;

    public SUnsupportedHandler(String message)
    {
        this.message = message;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) throws IOException
    {
        ProtocolUtil.sendMessage(connection, Protocol.MESSAGE, message);
    }

}
