package me.earth.earthhack.impl.modules.misc.antipackets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerSPacket extends
        ModuleListener<AntiPackets, PacketEvent.Receive<?>>
{
    public ListenerSPacket(AntiPackets module)
    {
        super(module, PacketEvent.Receive.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<?> event)
    {
        module.onPacket(event, true);
    }

}