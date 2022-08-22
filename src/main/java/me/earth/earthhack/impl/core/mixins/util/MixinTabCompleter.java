package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.TabCompleter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TabCompleter.class)
public abstract class MixinTabCompleter
{
    @Inject(method = "requestCompletions", at = @At("HEAD"), cancellable = true)
    public void requestCompletionsHook(String prefix, CallbackInfo ci)
    {
        if (Minecraft.getMinecraft().player == null)
        {
            ci.cancel();
        }
    }
    
}
