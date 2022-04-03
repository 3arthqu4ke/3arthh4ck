package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = GameSettings.Options.class)
public abstract class MixinOptions
{
    @ModifyConstant(
        method = "<clinit>",
        constant = @Constant(floatValue = 110.0f))
    private static float fov(float initial)
    {
        return 180.0f;
    }

    @ModifyConstant(
        method = "<clinit>",
        constant = @Constant(floatValue = 2.0f))
    private static float renderDistance(float initial)
    {
        return 1.0f;
    }

}
