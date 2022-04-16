package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity>
{
    @Shadow protected boolean renderOutlines;

    @Shadow public abstract void bindTexture(ResourceLocation location);

    @Shadow
    public void doRender(T entity, double x, double y,
                                  double z, float entityYaw,
                                  float partialTicks) {
        throw new IllegalStateException("doRender has not been shadowed!");
    }

    @Inject(
        method = "doRenderShadowAndFire",
        at = @At("HEAD"),
        cancellable = true)
    private void doRenderShadowAndFireHook(CallbackInfo info)
    {
        if (ESP.isRendering)
        {
            info.cancel();
        }
    }

    @Inject(
        method = "renderLivingLabel",
        at = @At("HEAD"),
        cancellable = true)
    private void renderLivingLabelHook(CallbackInfo info)
    {
        if (ESP.isRendering)
        {
            info.cancel();
        }
    }

}
