package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.TextComponentString;

public class ServerUtil implements Globals
{
    private static final ModuleCache<PingSpoof> PING_SPOOF =
            Caches.getModule(PingSpoof.class);
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    public static void disconnectFromMC(String message)
    {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null)
        {
            connection.getNetworkManager()
                      .closeChannel(new TextComponentString(message));
        }
    }

    public static int getPingNoPingSpoof()
    {
        int ping = getPing();
        if (PING_SPOOF.isEnabled())
        {
            ping -= PING_SPOOF.get().getDelay();
        }

        return ping;
    }

    public static int getPing()
    {
        if (PINGBYPASS.isEnabled() && !PingBypass.isServer())
        {
            return PINGBYPASS.get().getServerPing();
        }

        try
        {
            NetHandlerPlayClient connection = mc.getConnection();
            if (connection != null)
            {
                NetworkPlayerInfo info = connection
                        .getPlayerInfo(mc.getConnection()
                                         .getGameProfile()
                                         .getId());
                //noinspection ConstantConditions
                if (info != null)
                {
                    return info.getResponseTime();
                }
            }
        }
        catch (Throwable t)
        {
            // This can be called asynchronously so better be safe
            t.printStackTrace();
        }

        return 0;
    }

}
