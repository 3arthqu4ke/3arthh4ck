package me.earth.earthhack.forge.mixins.minecraftforge;

import me.earth.earthhack.impl.Earthhack;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FMLClientHandler.class, remap = false)
public abstract class MixinFMLClientHandler {
    @Inject(
        method = "finishMinecraftLoading",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fml/client/SplashProgress;finish()V",
            ordinal = 1,
            remap = false),
        remap = false)
    private void finishMinecraftLoadingHook(CallbackInfo ci) {
        Earthhack.init();
        Earthhack.postInit();
    }

}
