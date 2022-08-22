package me.earth.earthhack.impl.core.mixins.render.entity;

import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.client.renderer.entity.RenderGuardian;
import net.minecraft.entity.monster.EntityGuardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGuardian.class)
public abstract class MixinRenderGuardian
{
    /**
     * target = {@link EntityGuardian#getAttackAnimationScale(float)}
     */
    @Inject(
        method = "doRender",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/monster/EntityGuardian" +
                     ".getAttackAnimationScale(F)F"),
        cancellable = true)
    public void doRenderHook(EntityGuardian entity,
                              double x,
                              double y,
                              double z,
                              float entityYaw,
                              float partialTicks,
                              CallbackInfo info)
    {
        if (ESP.isRendering)
        {
            info.cancel();
        }
    }

}
