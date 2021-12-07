package me.earth.earthhack.vanilla.mixins;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.BlockLayerEvent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderChunk.class)
public abstract class MixinRenderChunk
{
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
        method = "rebuildChunk",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;getRenderLayer()Lnet/minecraft/util/BlockRenderLayer;"))
    private BlockRenderLayer getRenderLayerHook(Block block)
    {
        BlockLayerEvent event = new BlockLayerEvent(block);
        Bus.EVENT_BUS.post(event);
        if (event.getLayer() != null)
        {
            return event.getLayer();
        }

        return block.getRenderLayer();
    }

}
