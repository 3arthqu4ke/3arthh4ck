package me.earth.earthhack.impl.core.mixins.render.entity;

import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.client.renderer.entity.RenderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderDragon.class)
public abstract class MixinRenderDragon
{
    @Inject(
        method = "renderCrystalBeams",
        at = @At("HEAD"),
        cancellable = true)
    private static void renderCrystalBeamsHook(double x,
                                               double y,
                                               double z,
                                               float partialTicks,
                                               double entityX,
                                               double entityY,
                                               double entityZ,
                                               int entityTicks,
                                               double healingX,
                                               double healingY,
                                               double healingZ,
                                               CallbackInfo info)
    {
        if (ESP.isRendering)
        {
            info.cancel();
        }
    }

}
