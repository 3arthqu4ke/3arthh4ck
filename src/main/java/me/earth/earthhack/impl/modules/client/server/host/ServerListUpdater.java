package me.earth.earthhack.impl.modules.client.server.host;

import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionListener;
import me.earth.earthhack.impl.modules.client.server.api.IConnectionManager;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;

import java.io.IOException;

public final class ServerListUpdater implements IConnectionListener
{
    @Override
    public void onJoin(IConnectionManager manager, IConnection connection)
    {
        updateServerList(manager);
    }

    @Override
    public void onLeave(IConnectionManager manager, IConnection connection)
    {
        updateServerList(manager);
    }

    private void updateServerList(IConnectionManager manager)
    {
        byte[] serverList = ProtocolUtil.serializeServerList(manager);
        for (IConnection connection : manager.getConnections())
        {
            try
            {
                connection.send(serverList);
            }
            catch (IOException e)
            {
                manager.remove(connection);
                return;
            }
        }
    }

}
