package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.toasts.GuiToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiToast.class)
public abstract class MixinGuiToast
{
    private static final ModuleCache<NoRender> NO_RENDER =
        Caches.getModule(NoRender.class);

    @Inject(
        method = "drawToast",
        at = @At("HEAD"),
        cancellable = true)
    public void drawToastHook(ScaledResolution resolution, CallbackInfo info)
    {
        if (NO_RENDER.returnIfPresent(NoRender::noAdvancements, false))
        {
            info.cancel();
        }
    }

}
