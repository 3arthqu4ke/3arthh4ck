package me.earth.earthhack.impl.modules.render.sounds;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnMob extends
        ModuleListener<Sounds, PacketEvent.Receive<SPacketSpawnMob>>
{
    public ListenerSpawnMob(Sounds module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnMob.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnMob> event)
    {
        Entity player = mc.player;
        if (player != null && module.slimes.getValue())
        {
            SPacketSpawnMob p = event.getPacket();
            if (p.getEntityType() == 55
                    && p.getY() <= 40
                    && !mc.world.getBiome(player.getPosition())
                                .getBiomeName()
                                .toLowerCase()
                                .contains("swamp"))
            {
                BlockPos pos = new BlockPos(p.getX(), p.getY(), p.getZ());
                module.log("Slime: " + mc.world.getChunk(pos).x + "-ChunkX, "
                                     + mc.world.getChunk(pos).z + "-ChunkZ.");
            }
        }
    }

}
