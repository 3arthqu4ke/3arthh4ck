package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketEntityMetadata;

// TODO: this can allow us to make eating slightly faster etc.
final class ListenerSPacketEntityMetaData extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketEntityMetadata>>
{
    public ListenerSPacketEntityMetaData(Packets module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityMetadata.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityMetadata> event)
    {

    }

}
