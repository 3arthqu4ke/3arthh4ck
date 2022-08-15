package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.play.client.CPacketPlayerDigging;

public class PbPlayerDiggingListener extends EventListener<PacketEvent.Send<CPacketPlayerDigging>> {
    public PbPlayerDiggingListener() {
        super(PacketEvent.Send.class, Integer.MAX_VALUE /* runs before PingBypass.PACKET_MANAGER! */, CPacketPlayerDigging.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerDigging> event) {
        if (event.getPacket().getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM
            && PingBypass.PACKET_MANAGER.isUnAuthorized(event.getPacket())
            && PingBypass.isConnected()
            && PingBypass.PACKET_MANAGER.noThreadLocalFlag()) {
            event.setCancelled(true);
        }
    }

}
