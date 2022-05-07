package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.UnloadChunkEvent;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkProviderClient.class)
public abstract class MixinChunkProviderClient
{
    @Inject(method = "unloadChunk", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onUnloadChunkHook(int x, int z, CallbackInfo ci, Chunk chunk)
    {
        Bus.EVENT_BUS.post(new UnloadChunkEvent(chunk));
    }

}
