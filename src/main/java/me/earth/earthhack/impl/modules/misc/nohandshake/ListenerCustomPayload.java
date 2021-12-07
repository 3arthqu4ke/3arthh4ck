package me.earth.earthhack.impl.modules.misc.nohandshake;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketCustomPayload;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

final class ListenerCustomPayload extends
        ModuleListener<NoHandShake, PacketEvent.Send<CPacketCustomPayload>>
{
    public ListenerCustomPayload(NoHandShake module)
    {
        super(module, PacketEvent.Send.class, CPacketCustomPayload.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketCustomPayload> event)
    {
        CPacketCustomPayload packet = event.getPacket();
        if (packet.getChannelName().equals("MC|Brand"))
        {
            PacketBuffer buffer = packet.getBufferData();
            BufferUtil.releaseBuffer(buffer);

            ((ICPacketCustomPayload) packet).setData(
                new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));
        }
    }

}
