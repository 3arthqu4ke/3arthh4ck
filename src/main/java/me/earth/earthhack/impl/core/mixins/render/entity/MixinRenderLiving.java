package me.earth.earthhack.impl.core.mixins.render.entity;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLiving.class)
public abstract class MixinRenderLiving
{
    private static final ModuleCache<NoInterp> NOINTERP =
            Caches.getModule(NoInterp.class);

    @Inject(
        method = "renderLeash",
        at = @At("HEAD"),
        cancellable = true)
    public void renderLeashHook(CallbackInfo info)
    {
        if (ESP.isRendering)
        {
            info.cancel();
        }
    }

    @Redirect(
        method = "renderLeash",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posX:D"))
    public double posXHook(Entity entity)
    {
        return NoInterp.noInterpX(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderLeash",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posY:D"))
    public double posYHook(Entity entity)
    {
        return NoInterp.noInterpY(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderLeash",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posZ:D"))
    public double posZHook(Entity entity)
    {
        return NoInterp.noInterpZ(NOINTERP.get(), entity);
    }

}
