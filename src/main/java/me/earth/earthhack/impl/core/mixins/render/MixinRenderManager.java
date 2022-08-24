package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.render.IRenderManager;
import me.earth.earthhack.impl.event.events.render.RenderEntityEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReportedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager implements IRenderManager
{
    @Shadow private boolean renderOutlines;
    @Shadow private boolean debugBoundingBox;

    @Shadow
    protected abstract void renderDebugBoundingBox(Entity entityIn,
                                                   double x, double y,
                                                   double z, float entityYaw,
                                                   float partialTicks);

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
    public double posXHook0(Entity entity)
    {
        return NoInterp.noInterpX(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderEntityStatic",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posY:D",
            ordinal = 1))
    public double posYHook0(Entity entity)
    {
        return NoInterp.noInterpY(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderEntityStatic",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posZ:D",
            ordinal = 1))
    public double posZHook0(Entity entity)
    {
        return NoInterp.noInterpZ(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "cacheActiveRenderInfo",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posX:D",
            ordinal = 1))
    public double posXHook1(Entity entity)
    {
        return NoInterp.noInterpX(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "cacheActiveRenderInfo",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posY:D",
            ordinal = 1))
    public double posYHook1(Entity entity)
    {
        return NoInterp.noInterpY(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "cacheActiveRenderInfo",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posZ:D",
            ordinal = 1))
    public double posZHook1(Entity entity)
    {
        return NoInterp.noInterpZ(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderMultipass",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posX:D",
            ordinal = 1))
    public double posXHook2(Entity entity)
    {
        return NoInterp.noInterpX(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderMultipass",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posY:D",
            ordinal = 1))
    public double posYHook2(Entity entity)
    {
        return NoInterp.noInterpY(NOINTERP.get(), entity);
    }

    @Redirect(
        method = "renderMultipass",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;posZ:D",
            ordinal = 1))
    public double posZHook2(Entity entity)
    {
        return NoInterp.noInterpZ(NOINTERP.get(), entity);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
        method = "renderEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/Render;setRenderOutlines(Z)V",
            shift = At.Shift.AFTER),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    public void preRenderEntityHook(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo ci, Render<Entity> render) {
        RenderEntityEvent pre = new RenderEntityEvent.Pre(render, entityIn, x, y, z, yaw, partialTicks);
        Bus.EVENT_BUS.post(pre);
        if (pre.isCancelled()) {
            RenderEntityEvent post = new RenderEntityEvent.Post(render, entityIn);
            Bus.EVENT_BUS.post(post);

            try
            {
                if (!this.renderOutlines)
                {
                    render.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
                }
            }
            catch (Throwable throwable2)
            {
                throw new ReportedException(
                    CrashReport.makeCrashReport(throwable2, "Post-rendering entity in world"));
            }

            if (this.debugBoundingBox && !entityIn.isInvisible() && !p_188391_10_ && !Minecraft.getMinecraft().isReducedDebug())
            {
                try
                {
                    this.renderDebugBoundingBox(entityIn, x, y, z, yaw, partialTicks);
                }
                catch (Throwable throwable)
                {
                    throw new ReportedException(CrashReport.makeCrashReport(throwable, "Rendering entity hitbox in world"));
                }
            }

            ci.cancel();
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
        method = "renderEntity",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderOutlines:Z",
            ordinal = 1,
            shift = At.Shift.BEFORE),
        locals = LocalCapture.CAPTURE_FAILHARD)
    public void postRenderEntityHook(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo ci, Render<Entity> render) {
        RenderEntityEvent post = new RenderEntityEvent.Post(render, entityIn);
        Bus.EVENT_BUS.post(post);
    }

}
