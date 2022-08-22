package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.impl.core.ducks.util.IKeyBinding;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements IKeyBinding
{
    @Shadow
    private boolean pressed;

    @Override
    @Accessor(value = "pressed")
    public abstract void setPressed(boolean delay);

    @Inject(
        method = "isKeyDown",
        at = @At("RETURN"),
        cancellable = true)
    public void isKeyDownHook(CallbackInfoReturnable<Boolean> info)
    {
        if (!info.getReturnValue())
        {
            info.setReturnValue(this.pressed);
        }
    }

}
