package me.earth.earthhack.impl.modules.client.media;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.pingbypass.packets.PayloadIDs;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

final class ListenerTick extends ModuleListener<Media, TickEvent>
{
    public ListenerTick(Media module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (Media.PING_BYPASS.isEnabled() && Media.PING_BYPASS.get().isOld())
        {
            if (!module.pingBypassEnabled)
            {
                module.cache.clear();
            }

            module.pingBypassEnabled = true;
            if (mc.player == null)
            {
                module.send = false;
            }
            else if (!module.send)
            {
                PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                buffer.writeShort(PayloadIDs.NAME);
                mc.player.connection.sendPacket(
                        new CPacketCustomPayload("PingBypass", buffer));

                // BufferUtil.releaseBuffer(buffer);
                module.send = true;
            }
        }
        else if (module.pingBypassEnabled)
        {
            module.pingBypassEnabled = false;
            module.cache.clear();
        }
    }

}
