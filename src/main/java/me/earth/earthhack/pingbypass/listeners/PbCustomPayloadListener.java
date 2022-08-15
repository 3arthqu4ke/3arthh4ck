package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.pingbypass.event.PbPacketEvent;
import net.minecraft.network.play.client.CPacketCustomPayload;

public class PbCustomPayloadListener extends EventListener<PbPacketEvent.C2S<CPacketCustomPayload>> {
    public PbCustomPayloadListener() {
        super(PbPacketEvent.C2S.class, CPacketCustomPayload.class);
    }

    @Override
    public void invoke(PbPacketEvent.C2S<CPacketCustomPayload> event) {
        if ("MC|Brand".equals(event.getPacket().getChannelName())) {
            event.setCancelled(true);
        } else if (!event.getPacket().getChannelName().equalsIgnoreCase("PingBypass")) {
            Earthhack.getLogger().info("CustomPayload: " + event.getPacket().getChannelName());
            if (!event.getPacket().getChannelName().startsWith("MC|")) {
                Earthhack.getLogger().warn("Cancelled CustomPayload: "
                                               + event.getPacket().getChannelName());
                event.setCancelled(true);
            }
        }
    }

}
