package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.render.IRenderManager;
import me.earth.earthhack.impl.event.events.render.RenderEntityEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager implements IRenderManager
{
    private static final ModuleCache<NoInterp> NOINTERP =
            Caches.getModule(NoInterp.class);

    @Accessor(value = "renderPosX")
    public abstract double getRenderPosX();

    @Accessor(value = "renderPosY")
    public abstract double getRenderPosY();

    @Accessor(value = "renderPosZ")
    public abstract double getRenderPosZ();

    @Redirect(
        method = "renderEntityStatic",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posX:D",
            ordinal = 1))
    private double posXHook0(Entity entity)
    {
        return NoInterp.noInterpX(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderEntityStatic",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posY:D",
            ordinal = 1))
    private double posYHook0(Entity entity)
    {
        return NoInterp.noInterpY(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderEntityStatic",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posZ:D",
            ordinal = 1))
    private double posZHook0(Entity entity)
    {
        return NoInterp.noInterpZ(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "cacheActiveRenderInfo",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posX:D",
            ordinal = 1))
    private double posXHook1(Entity entity)
    {
        return NoInterp.noInterpX(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "cacheActiveRenderInfo",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posY:D",
            ordinal = 1))
    private double posYHook1(Entity entity)
    {
        return NoInterp.noInterpY(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "cacheActiveRenderInfo",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posZ:D",
            ordinal = 1))
    private double posZHook1(Entity entity)
    {
        return NoInterp.noInterpZ(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderMultipass",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posX:D",
            ordinal = 1))
    private double posXHook2(Entity entity)
    {
        return NoInterp.noInterpX(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderMultipass",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posY:D",
            ordinal = 1))
    private double posYHook2(Entity entity)
    {
        return NoInterp.noInterpY(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderMultipass",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posZ:D",
            ordinal = 1))
    private double posZHook2(Entity entity)
    {
        return NoInterp.noInterpZ(NOINTERP.get(), entity);
    }


    @Redirect(method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/Render;doRender(Lnet/minecraft/entity/Entity;DDDFF)V"))
    public void renderEntity(Render<Entity> render, Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        final RenderEntityEvent pre = new RenderEntityEvent.Pre(render, entity, x, y, z, entityYaw, partialTicks);
        Bus.EVENT_BUS.post(pre);
        if (!pre.isCancelled()) {
            render.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
        final RenderEntityEvent post = new RenderEntityEvent.Post(render, entity);
        Bus.EVENT_BUS.post(post);
    }
}
