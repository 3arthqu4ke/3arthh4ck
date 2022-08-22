package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer
{
    @Shadow
    public abstract boolean isSpectator();

    private static final ModuleCache<NoRender>
        NO_RENDER = Caches.getModule(NoRender.class);

    @Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
    public void getFovModifierHook(CallbackInfoReturnable<Float> info)
    {
        if (NO_RENDER.returnIfPresent(NoRender::dynamicFov, false))
        {
            info.setReturnValue(1.0f);
        }
    }

}
