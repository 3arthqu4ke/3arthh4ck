package me.earth.earthhack.impl.core.mixins.settings;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding
{
    @Inject(method = "unpressKey", at = @At("HEAD"))
    public void onUnpress(CallbackInfo info)
    {
        // Managers.KEYBOARD.onUnpress(KeyBinding.class.cast(this));
    }

}
