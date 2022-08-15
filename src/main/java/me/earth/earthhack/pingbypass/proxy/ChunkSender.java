package me.earth.earthhack.pingbypass.proxy;

import me.earth.earthhack.impl.core.mixins.util.IChunkProviderClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkSender {
    public static void sendChunks(World world, NetworkManager manager) {
        if (world.getChunkProvider() instanceof IChunkProviderClient) {
            for (Chunk chunk : ((IChunkProviderClient) world.getChunkProvider()).getLoadedChunks().values()) {
                if (chunk != null) {
                    manager.sendPacket(new SPacketChunkData(chunk, 65535));
                }
            }
        }
    }

}
