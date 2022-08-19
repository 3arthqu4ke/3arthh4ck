package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.play.server.SPacketCustomPayload;

/**
 * Prevents servers from trolling us with PingBypass packets.
 */
public class PbAntiTrollListener
    extends EventListener<PacketEvent.Receive<SPacketCustomPayload>> {
    public PbAntiTrollListener() {
        super(PacketEvent.Receive.class,
              Integer.MAX_VALUE,
              SPacketCustomPayload.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketCustomPayload> event) {
        if ("PingBypass".equalsIgnoreCase(event.getPacket().getChannelName())
                && (PingBypass.isServer() || !PingBypassModule.CACHE.isEnabled()))
        {
            Earthhack.getLogger().warn("Received unexpected PingBypass CustomPayload!");
            event.setCancelled(true);
            event.setPingBypassCancelled(true);
        }
    }

}
