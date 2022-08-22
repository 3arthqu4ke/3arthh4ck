package me.earth.earthhack.impl.core.mixins.render.chunk;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.debug.Debug;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderChunk.class)
public abstract class MixinRenderChunk
{
    private static final SettingCache<Boolean, BooleanSetting, Debug> SLOW =
     Caches.getSetting(Debug.class, BooleanSetting.class, "SlowUpdates", false);

    @Inject(
        method = "needsImmediateUpdate",
        at = @At(value = "HEAD"),
        cancellable = true)
    public void needsImmediateUpdateHook(CallbackInfoReturnable<Boolean> cir)
    {
        if (SLOW.getValue())
        {
            cir.setReturnValue(false);
        }
    }

}
