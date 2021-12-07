package me.earth.earthhack.impl.core.mixins.render.entity;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.CrystalRenderEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.crystalchams.CrystalChams;
import me.earth.earthhack.impl.modules.render.crystalscale.CrystalScale;
import me.earth.earthhack.impl.modules.render.handchams.modes.ChamsMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_LINE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;

@Mixin(RenderEnderCrystal.class)
public abstract class MixinRenderEnderCrystal {

    private static final ModuleCache<CrystalScale> SCALE =
            Caches.getModule(CrystalScale.class);
    private static final ModuleCache<CrystalChams> CHAMS =
            Caches.getModule(CrystalChams.class);

    @Redirect(
            method = "doRender",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V")
    )
    public void renderHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!SCALE.isPresent()) {
            return;
        }

        float crystalScale = SCALE.get().animate.getValue() ?
                (float) (SCALE.get().scaleMap.containsKey(entityIn.getEntityId()) ? SCALE.get().scaleMap.get(entityIn.getEntityId()).getCurrent() : 0.1f) :
                SCALE.get().scale.getValue();
        TimeAnimation animation = SCALE.get().scaleMap.get(entityIn.getEntityId());
        if (animation != null) animation.add(Minecraft.getMinecraft().getRenderPartialTicks());
        if (SCALE.isEnabled())
        {
            GlStateManager.scale(crystalScale, crystalScale, crystalScale);
        }

        /*if (CHAMS.isEnabled()) {
            if (CHAMS.get().mode.getValue() == ChamsMode.Gradient) {
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glEnable(GL_BLEND);
                glDisable(GL_LIGHTING);
                glDisable(GL_TEXTURE_2D);
                float alpha = CHAMS.get().color.getValue().getAlpha() / 255.0f;
                glColor4f(1.0f, 1.0f, 1.0f, alpha);
                modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                glEnable(GL_TEXTURE_2D);

                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                float f = (float)entityIn.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/rainbow.png"));
                Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
                GlStateManager.enableBlend();
                GlStateManager.depthFunc(514);
                GlStateManager.depthMask(false);
                GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);

                for (int i = 0; i < 2; ++i)
                {
                    GlStateManager.disableLighting();
                    // GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.loadIdentity();
                    GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
                    GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 0.5F);
                    GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                    GlStateManager.matrixMode(5888);
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                }

                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.depthFunc(515);
                GlStateManager.disableBlend();
                Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
                glPopAttrib();
            } else {
                if (CHAMS.get().wireframe.getValue()) {
                    Color wireColor = CHAMS.get().wireFrameColor.getValue();
                    glPushAttrib(GL_ALL_ATTRIB_BITS);
                    glEnable(GL_BLEND);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    if (CHAMS.get().wireWalls.getValue()) {
                        glDepthMask(false);
                        glDisable(GL_DEPTH_TEST);
                    }
                    glColor4f(wireColor.getRed() / 255.0f, wireColor.getGreen() / 255.0f, wireColor.getBlue() / 255.0f, wireColor.getAlpha() / 255.0f);
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    glPopAttrib();
                }

                if (CHAMS.get().chams.getValue()) {
                    Color chamsColor = CHAMS.get().color.getValue();
                    glPushAttrib(GL_ALL_ATTRIB_BITS);
                    glEnable(GL_BLEND);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_LIGHTING);
                    glDisable(GL_ALPHA_TEST);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glEnable(GL_STENCIL_TEST);
                    glEnable(GL_POLYGON_OFFSET_LINE);
                    if (CHAMS.get().throughWalls.getValue()) {
                        glDepthMask(false);
                        glDisable(GL_DEPTH_TEST);
                    }
                    glColor4f(chamsColor.getRed() / 255.0f, chamsColor.getGreen() / 255.0f, chamsColor.getBlue() / 255.0f, chamsColor.getAlpha() / 255.0f);
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    glPopAttrib();
                }
            }
        } else {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }*/
        RenderEnderCrystal renderLiving = RenderEnderCrystal.class.cast(this);
        CrystalRenderEvent.Pre pre = new CrystalRenderEvent.Pre(renderLiving, entityIn, modelBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Bus.EVENT_BUS.post(pre);
        if (!pre.isCancelled()) modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        CrystalRenderEvent.Post post = new CrystalRenderEvent.Post(renderLiving, entityIn, modelBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Bus.EVENT_BUS.post(post);

        if (SCALE.isEnabled()) {
            GlStateManager.scale(1 / crystalScale, 1 / crystalScale, 1 / crystalScale);
        }
    }

}
