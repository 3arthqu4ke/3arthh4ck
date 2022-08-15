package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.server.SPacketResourcePackSend;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class PbResourcePackListener
    extends EventListener<PacketEvent.Receive<SPacketResourcePackSend>>
    implements Globals {
    public PbResourcePackListener() {
        super(PacketEvent.Receive.class, Integer.MAX_VALUE, SPacketResourcePackSend.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketResourcePackSend> event) {
        NetHandlerPlayClient client = mc.getConnection();
        if (client != null) {
            SPacketResourcePackSend packet = event.getPacket();
            event.setPingBypassCancelled(onPacket(packet, client));
            event.setCancelled(true);
        }
    }

    private boolean onPacket(SPacketResourcePackSend packetIn, NetHandlerPlayClient client) {
        String s = packetIn.getURL();
        if (this.validateResourcePackUrl(s, client))
        {
            if (!PingBypass.CONFIG.validateResourcePacksFurther())
            {
                return false;
            }

            if (s.startsWith("level://"))
            {
                try
                {
                    String s2 = URLDecoder.decode(s.substring("level://".length()), StandardCharsets.UTF_8.toString());
                    File file1 = new File(mc.gameDir, "saves");
                    File file2 = new File(file1, s2);

                    if (file2.isFile())
                    {
                        return false;
                    }
                }
                catch (UnsupportedEncodingException ignored)
                {

                }

                client.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                return true;
            }
            else
            {
                ServerData serverdata = mc.getCurrentServerData();
                if (serverdata != null && serverdata.getResourceMode() == ServerData.ServerResourceMode.DISABLED)
                {
                    client.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    private boolean validateResourcePackUrl(String url, NetHandlerPlayClient client)
    {
        try
        {
            URI uri = new URI(url);
            String s = uri.getScheme();
            boolean flag = "level".equals(s);

            if (!"http".equals(s) && !"https".equals(s) && !flag)
            {
                throw new URISyntaxException(url, "Wrong protocol");
            }
            else if (!flag || !url.contains("..") && url.endsWith("/resources.zip"))
            {
                return true;
            }
            else
            {
                throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
            }
        }
        catch (URISyntaxException var5)
        {
            client.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            return false;
        }
    }

}
