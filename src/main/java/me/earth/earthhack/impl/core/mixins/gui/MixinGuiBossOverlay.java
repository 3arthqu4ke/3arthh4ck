package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.gui.GuiBossOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiBossOverlay.class)
public abstract class MixinGuiBossOverlay {

    private static final ModuleCache<NoRender> NO_RENDER =
            Caches.getModule(NoRender.class);

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    public void renderHook(CallbackInfo ci)
    {
        if (NO_RENDER.get().boss.getValue())
        {
            ci.cancel();
        }
    }

}
