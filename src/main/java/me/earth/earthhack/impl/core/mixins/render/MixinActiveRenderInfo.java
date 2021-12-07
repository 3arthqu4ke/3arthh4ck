package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ActiveRenderInfo.class)
public abstract class MixinActiveRenderInfo
{
    private static final ModuleCache<NoInterp> NOINTERP =
            Caches.getModule(NoInterp.class);

    @Redirect(
        method = "projectViewFromEntity",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posX:D",
            ordinal = 1))
    private static double posXHook(Entity entity)
    {
        return NoInterp.noInterpX(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "projectViewFromEntity",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posY:D",
            ordinal = 1))
    private static double posYHook(Entity entity)
    {
        return NoInterp.noInterpY(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "projectViewFromEntity",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posZ:D",
            ordinal = 1))
    private static double posZHook(Entity entity)
    {
        return NoInterp.noInterpZ(NOINTERP.get(), entity);
    }

}
