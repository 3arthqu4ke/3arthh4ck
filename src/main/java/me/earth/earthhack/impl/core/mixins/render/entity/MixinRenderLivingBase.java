package me.earth.earthhack.impl.core.mixins.render.entity;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import me.earth.earthhack.impl.modules.render.chams.mode.ChamsMode;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static me.earth.earthhack.impl.managers.Managers.ROTATION;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase {
    private static final ModuleCache<Spectate> SPECTATE =
        Caches.getModule(Spectate.class);
    private static final ModuleCache<Chams> CHAMS =
        Caches.getModule(Chams.class);
    private static final ModuleCache<ESP> ESP_MODULE =
        Caches.getModule(ESP.class);

    private float prevRenderYawOffset;
    private float renderYawOffset;
    private float prevRotationYawHead;
    private float rotationYawHead;
    private float prevRotationPitch;
    private float rotationPitch;

    @Inject(method = "doRender", at = @At("HEAD"))
    public void doRenderHookHead(EntityLivingBase entity, double x, double y,
                                  double z, float entityYaw, float partialTicks,
                                  CallbackInfo info) {
        if (entity instanceof EntityPlayerSP ||
            SPECTATE.isEnabled() && entity.equals(SPECTATE.get().getFake())) {
            prevRenderYawOffset = entity.prevRenderYawOffset;
            renderYawOffset = entity.renderYawOffset;
            prevRotationYawHead = entity.prevRotationYawHead;
            rotationYawHead = entity.rotationYawHead;
            prevRotationPitch = entity.prevRotationPitch;
            rotationPitch = entity.rotationPitch;

            entity.prevRenderYawOffset = ROTATION.getPrevRenderYawOffset();
            entity.renderYawOffset = ROTATION.getRenderYawOffset();
            entity.prevRotationYawHead = ROTATION.getPrevRotationYawHead();
            entity.rotationYawHead = ROTATION.getRotationYawHead();
            entity.prevRotationPitch = ROTATION.getPrevPitch();
            entity.rotationPitch = ROTATION.getRenderPitch();
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    public void doRenderHookReturn(EntityLivingBase entity, double x, double y,
                                    double z, float entityYaw,
                                    float partialTicks,
                                    CallbackInfo info) {
        if (entity instanceof EntityPlayerSP ||
            SPECTATE.isEnabled() && entity.equals(SPECTATE.get().getFake())) {
            entity.prevRenderYawOffset = prevRenderYawOffset;
            entity.renderYawOffset = renderYawOffset;
            entity.prevRotationYawHead = prevRotationYawHead;
            entity.rotationYawHead = rotationYawHead;
            entity.prevRotationPitch = prevRotationPitch;
            entity.rotationPitch = rotationPitch;
        }
    }

    @Inject(
        method = "renderLayers",
        at = @At("HEAD"),
        cancellable = true)
    public void renderLayersHook(CallbackInfo info) {
        if (ESP.isRendering) {
            info.cancel();
        }
    }

    @Inject(
        method = "renderName",
        at = @At("HEAD"),
        cancellable = true)
    public void renderNameHook(EntityLivingBase entity,
                                double x,
                                double y,
                                double z,
                                CallbackInfo info) {
        if (ESP.isRendering) {
            info.cancel();
        }
    }

    @Redirect(
        method = "setBrightness",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/EntityLivingBase;hurtTime:I"))
    public int hurtTimeHook(EntityLivingBase base) {
        if (!ESP_MODULE.returnIfPresent(ESP::shouldHurt, false)) {
            return 0;
        }

        return base.hurtTime;
    }

    @Inject(
        method = "doRender",
        at = @At("HEAD"))
    public void doRender_Pre(EntityLivingBase entity,
                             double x,
                             double y,
                             double z,
                             float entityYaw,
                             float partialTicks,
                             CallbackInfo info) {
        if (CHAMS.returnIfPresent(c ->
                                      c.isValid(entity, ChamsMode.Normal),
                                  false)) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0f, -1100000.0f);
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    public void doRender_Post(EntityLivingBase entity,
                              double x,
                              double y,
                              double z,
                              float entityYaw,
                              float partialTicks,
                              CallbackInfo info) {
        if (CHAMS.returnIfPresent(c ->
                                      c.isValid(entity, ChamsMode.Normal),
                                  false)) {
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0f, 1100000.0f);
        }
    }

    @Inject(method = "renderModel", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V", shift = At.Shift.BEFORE), cancellable = true)
    private void preRenderModel(EntityLivingBase entitylivingbaseIn,
                                float limbSwing, float limbSwingAmount,
                                float ageInTicks, float netHeadYaw,
                                float headPitch, float scaleFactor,
                                CallbackInfo ci, boolean flag, boolean flag1) {
        RenderLivingBase<?> renderLiving = RenderLivingBase.class.cast(this);
        ModelRenderEvent event = new ModelRenderEvent.Pre(renderLiving,
                                                          entitylivingbaseIn,
                                                          renderLiving.getMainModel(),
                                                          limbSwing,
                                                          limbSwingAmount,
                                                          ageInTicks,
                                                          netHeadYaw, headPitch,
                                                          scaleFactor);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            Bus.EVENT_BUS.post(
                new ModelRenderEvent.Post(renderLiving, entitylivingbaseIn,
                                          renderLiving.getMainModel(),
                                          limbSwing, limbSwingAmount,
                                          ageInTicks, netHeadYaw, headPitch,
                                          scaleFactor));
            if (flag1) {
                GlStateManager.disableBlendProfile(
                    GlStateManager.Profile.TRANSPARENT_MODEL);
            }

            ci.cancel();
        }
    }

    @Inject(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V", shift = At.Shift.AFTER))
    private void postRenderModel(EntityLivingBase entity, float limbSwing,
                                 float limbSwingAmount, float ageInTicks,
                                 float netHeadYaw, float headPitch,
                                 float scaleFactor, CallbackInfo ci) {
        RenderLivingBase<?> renderLiving = RenderLivingBase.class.cast(this);
        Bus.EVENT_BUS.post(new ModelRenderEvent.Post(renderLiving, entity,
                                                     renderLiving.getMainModel(),
                                                     limbSwing, limbSwingAmount,
                                                     ageInTicks, netHeadYaw,
                                                     headPitch, scaleFactor));
    }

}
