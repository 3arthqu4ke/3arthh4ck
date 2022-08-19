package me.earth.earthhack.impl.core.mixins.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.UnloadChunkEvent;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkProviderClient.class)
public abstract class MixinChunkProviderClient
{
    // needed to solve with redirect because vanilla optifine breaks local capture
    @Redirect(
        method = "unloadChunk",
        at = @At(value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;remove(J)Ljava/lang/Object;",
            remap = false))
    private Object removeHook(Long2ObjectMap<Chunk> loadedChunks, long l)
    {
        Chunk chunk = loadedChunks.remove(l);
        if (chunk != null)
        {
            Bus.EVENT_BUS.post(new UnloadChunkEvent(chunk));
        }

        return chunk;
    }

}
