package me.earth.earthhack.forge.mixins.block;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.BlockLayerEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock
{
    @Dynamic
    @Inject(
        method = "canRenderInLayer",
        at = @At("RETURN"),
        cancellable = true,
        remap = false)
    private void canRenderInLayerHook(IBlockState state,
                                      BlockRenderLayer layer,
                                      CallbackInfoReturnable<Boolean> info)
    {
        Block block = Block.class.cast(this);
        BlockLayerEvent event = new BlockLayerEvent(block);
        Bus.EVENT_BUS.post(event);

        if (event.getLayer() != null)
        {
            info.setReturnValue(event.getLayer() == layer);
        }
    }

}
