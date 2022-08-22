package me.earth.earthhack.impl.core.mixins.render.entity;

import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.client.renderer.entity.RenderMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderMinecart.class)
public abstract class MixinRenderMinecart
{
    @Inject(
        method = "renderCartContents",
        at = @At("HEAD"),
        cancellable = true)
    public void renderCartContentsHook(CallbackInfo info)
    {
        if (ESP.isRendering)
        {
            info.cancel();
        }
    }

}
