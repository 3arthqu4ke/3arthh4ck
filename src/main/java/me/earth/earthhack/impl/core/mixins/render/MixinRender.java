package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public abstract class MixinRender
{
    @Inject(
        method = "doRenderShadowAndFire",
        at = @At("HEAD"),
        cancellable = true)
    public void doRenderShadowAndFireHook(CallbackInfo info)
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
    public void renderLivingLabelHook(CallbackInfo info)
    {
        if (ESP.isRendering)
        {
            info.cancel();
        }
    }

}
