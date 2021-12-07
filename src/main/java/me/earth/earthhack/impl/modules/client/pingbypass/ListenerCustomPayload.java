package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.client.pingbypass.packets.PayloadManager;
import net.minecraft.network.play.server.SPacketCustomPayload;

final class ListenerCustomPayload extends
        EventListener<PacketEvent.Receive<SPacketCustomPayload>>
{
    private final PayloadManager manager;

    public ListenerCustomPayload(PayloadManager manager)
    {
        super(PacketEvent.Receive.class, SPacketCustomPayload.class);
        this.manager = manager;
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketCustomPayload> event)
    {
        if (event.getPacket().getChannelName().equalsIgnoreCase("PingBypass"))
        {
            try
            {
                manager.onPacket(event.getPacket());
                event.setCancelled(true);
            }
            finally
            {
                BufferUtil.releaseBuffer(event.getPacket().getBufferData());
            }
        }
    }

}
