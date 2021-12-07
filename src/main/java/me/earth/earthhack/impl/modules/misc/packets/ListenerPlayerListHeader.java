package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;

import java.util.Objects;

final class ListenerPlayerListHeader extends
        ModuleListener<Packets,
                       PacketEvent.Receive<SPacketPlayerListHeaderFooter>>
{
    public ListenerPlayerListHeader(Packets module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE + 1,
                SPacketPlayerListHeaderFooter.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerListHeaderFooter> event)
    {
        if (module.safeHeaders.getValue())
        {
            SPacketPlayerListHeaderFooter packet = event.getPacket();
            if (packet.getHeader().getFormattedText().isEmpty()
                    || packet.getFooter().getFormattedText().isEmpty())
            {
                event.setCancelled(true);
                mc.addScheduledTask(() ->
                    packet.processPacket(
                        Objects.requireNonNull(mc.getConnection())));
            }
        }
    }

}
