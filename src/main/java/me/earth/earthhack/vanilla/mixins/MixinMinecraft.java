package me.earth.earthhack.vanilla.mixins;

import me.earth.earthhack.impl.Earthhack;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V",
            ordinal = 2,
            shift = At.Shift.BEFORE))
    private void initHook(CallbackInfo ci)
    {
        Earthhack.init();
        Earthhack.postInit();
    }

}
