package me.earth.earthhack.impl.modules.render.newchunks;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.newchunks.util.ChunkData;
import net.minecraft.network.play.server.SPacketChunkData;

final class ListenerChunkData extends
        ModuleListener<NewChunks, PacketEvent.Receive<SPacketChunkData>>
{
    public ListenerChunkData(NewChunks module)
    {
        super(module, PacketEvent.Receive.class, SPacketChunkData.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketChunkData> event)
    {
        SPacketChunkData p = event.getPacket();
        if (!p.isFullChunk())
        {
            ChunkData data = new ChunkData(p.getChunkX(), p.getChunkZ());
            module.data.add(data);
        }
    }

}
