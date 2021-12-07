package me.earth.earthhack.forge.mixins.render;

import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveRenderInfo.class)
public abstract class MixinActiveRenderInfo
{
    @Inject(
        method = "updateRenderInfo(Lnet/minecraft/entity/Entity;Z)V",
        at = @At("HEAD"),
        remap = false)
    private static void updateRenderInfo(Entity entityplayerIn,
                                         boolean p_74583_1_,
                                         CallbackInfo ci)
    {
        RenderUtil.updateMatrices();
    }

}
