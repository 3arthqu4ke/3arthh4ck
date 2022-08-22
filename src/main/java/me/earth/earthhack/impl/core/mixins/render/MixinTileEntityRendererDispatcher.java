package me.earth.earthhack.impl.core.mixins.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.spongepowered.asm.mixin.Mixin;

// TODO: remove
@Mixin(TileEntityRendererDispatcher.class)
public abstract class MixinTileEntityRendererDispatcher
{
    /*
    @Inject(
            method = "render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V",
            at = @At("HEAD")
    )
    public void renderHook(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage, float p_192854_10_, CallbackInfo ci)
    {

        // GlStateManager.color();
    }
    */
}
