package me.earth.exampleplugin.mixin;

import me.earth.earthhack.impl.Earthhack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Earthhack.class, remap = false)
public class MixinExample
{
    @Inject(method = "init", at = @At("HEAD"))
    private static void initHook(CallbackInfo info)
    {
        System.out.println("Hello from the ExamplePlugins Mixins!");
    }

}
