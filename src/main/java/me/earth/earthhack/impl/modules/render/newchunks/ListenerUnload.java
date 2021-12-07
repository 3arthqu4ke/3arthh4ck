package me.earth.earthhack.impl.modules.render.newchunks;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketUnloadChunk;

final class ListenerUnload extends
        ModuleListener<NewChunks, PacketEvent.Receive<SPacketUnloadChunk>>
{
    public ListenerUnload(NewChunks module)
    {
        super(module, PacketEvent.Receive.class, SPacketUnloadChunk.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketUnloadChunk> event)
    {

    }

}
