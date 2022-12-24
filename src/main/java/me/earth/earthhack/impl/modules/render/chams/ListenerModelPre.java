package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.impl.core.mixins.render.entity.IEntityRenderer;
import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.chams.mode.ChamsMode;
import me.earth.earthhack.impl.modules.render.chams.mode.WireFrameMode;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.util.math.Vec2d;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

final class ListenerModelPre extends ModuleListener<Chams, ModelRenderEvent.Pre> {

    public ListenerModelPre(Chams module) {
        super(module, ModelRenderEvent.Pre.class);
    }

    @Override
    public void invoke(ModelRenderEvent.Pre event) {
        if (!ESP.isRendering && (module.wireframe.getValue() == WireFrameMode.Pre || module.wireframe.getValue() == WireFrameMode.All)) {
            module.doWireFrame(event);
        }

        if (!ESP.isRendering && module.mode.getValue() == ChamsMode.CSGO) {
            EntityLivingBase entity = event.getEntity();
            if (module.isValid(entity)) {
                event.setCancelled(true);
                boolean lightning = glIsEnabled(GL_LIGHTING);
                boolean blend = glIsEnabled(GL_BLEND);
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glDisable(GL_ALPHA_TEST);

                if (!module.texture.getValue()) {
                    glDisable(GL_TEXTURE_2D);
                }

                if (lightning) {
                    glDisable(GL_LIGHTING);
                }

                if (!blend) {
                    glEnable(GL_BLEND);
                }

                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                if (module.xqz.getValue()) {
                    glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                    glDepthMask(false);
                    glDisable(GL_DEPTH_TEST);
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);

                    render(event);
                }

                glDisable(GL_BLEND);
                glEnable(GL_DEPTH_TEST);
                glDepthMask(true);
                glEnable(GL_LIGHTING);

                if (!module.texture.getValue()) {
                    glEnable(GL_TEXTURE_2D);
                }

                glEnable(GL_ALPHA_TEST);
                glPopAttrib();
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glDisable(GL_ALPHA_TEST);

                if (!module.texture.getValue()) {
                    glDisable(GL_TEXTURE_2D);
                }

                glDisable(GL_LIGHTING);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);

                render(event);

                if (!blend) {
                    glDisable(GL_BLEND);
                }

                glEnable(GL_DEPTH_TEST);
                glDepthMask(true);

                if (lightning) {
                    glEnable(GL_LIGHTING);
                }

                if (!module.texture.getValue()) {
                    glEnable(GL_TEXTURE_2D);
                }

                glEnable(GL_ALPHA_TEST);
                glPopAttrib();
            }
        } else if (!ESP.isRendering && module.mode.getValue() == ChamsMode.Better && event.getEntity() instanceof EntityPlayer) {
            event.setCancelled(true);
            Color color = module.getVisibleColor(event.getEntity());
            Color wallsColor = module.getWallsColor(event.getEntity());
            glPushMatrix();
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glDisable(GL_ALPHA_TEST);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);
            glLineWidth(1.5f);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_STENCIL_TEST);
            glEnable(GL_POLYGON_OFFSET_LINE);
            glDepthMask(false);
            glDisable(GL_DEPTH_TEST);
            glColor4f(wallsColor.getRed() / 255.0f, wallsColor.getGreen() / 255.0f, wallsColor.getBlue() / 255.0f, wallsColor.getAlpha() / 255.0f);
            render(event);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            if (module.xqz.getValue()) {
                glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
                render(event);
            }
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glEnable(GL_ALPHA_TEST);
            glPopAttrib();
            glPopMatrix();
        } else if (!ESP.isRendering) {
            EntityLivingBase entity = event.getEntity();
            if (module.isValid(entity)) {
                if (module.mode.getValue() == ChamsMode.JelloBottom) {
                    event.setCancelled(true);
                    render(event);
                    Color color = module.getVisibleColor(event.getEntity());
                    glPushMatrix();
                    glPushAttrib(GL_ALL_ATTRIB_BITS);
                    glDisable(GL_ALPHA_TEST);
                    if (!module.texture.getValue())
                    {
                        glDisable(GL_TEXTURE_2D);
                    }

                    glDisable(GL_LIGHTING);
                    glEnable(GL_BLEND);
                    glLineWidth(1.5f);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glEnable(GL_STENCIL_TEST);
                    glEnable(GL_POLYGON_OFFSET_LINE);
                    glDepthMask(false);
                    glDisable(GL_DEPTH_TEST);
                    glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
                    render(event);
                    glDepthMask(true);
                    glEnable(GL_DEPTH_TEST);
                    if (!module.texture.getValue())
                    {
                        glEnable(GL_TEXTURE_2D);
                    }

                    glEnable(GL_LIGHTING);
                    glDisable(GL_BLEND);
                    glEnable(GL_ALPHA_TEST);
                    glPopAttrib();
                    glPopMatrix();
                }
            }
        }

        if (module.mode.getValue() == ChamsMode.FireShader
                && !ESP.isRendering
                && module.fireShader != null)
        {
            if (!module.isValid(event.getEntity())) return;
            event.setCancelled(true);
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glPushMatrix();
            Color color = module.getVisibleColor(event.getEntity());
            module.fireShader.bind();
            module.fireShader.set("time", (System.currentTimeMillis() - module.initTime) / 2000.0f);
            module.fireShader.set("resolution", new Vec2f((mc.displayWidth * 2) /*/ 20.0f*/, (mc.displayHeight * 2) /*/ 20.0f*/));
            module.fireShader.set("tex", 0);

            GlStateManager.pushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, color.getAlpha() / 255.0f);
            module.fireShader.set("alpha", color.getAlpha() / 255.0f);
            glEnable(GL_POLYGON_OFFSET_FILL);
            glEnable(GL_BLEND);
            glPolygonOffset(1.0f, -2000000f);
            render(event);
            glDisable(GL_BLEND);
            glDisable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, 2000000f);
            GlStateManager.popMatrix();
            module.fireShader.unbind();
            glPopMatrix();
            glPopAttrib();
        }

        if (module.mode.getValue() == ChamsMode.GalaxyShader
                && !ESP.isRendering
                && module.galaxyShader != null)
        {
            if (!module.isValid(event.getEntity())) return;
            event.setCancelled(true);
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glPushMatrix();
            Color color = module.getVisibleColor(event.getEntity());
            module.galaxyShader.bind();
            module.galaxyShader.set("time", (System.currentTimeMillis() - module.initTime) / 2000.0f);
            module.galaxyShader.set("resolution", new Vec2f((mc.displayWidth * 2) /*/ 20.0f*/, (mc.displayHeight * 2) /*/ 20.0f*/));
            module.galaxyShader.set("tex", 0);

            GlStateManager.pushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, color.getAlpha() / 255.0f);
            module.galaxyShader.set("alpha", color.getAlpha() / 255.0f);
            glEnable(GL_BLEND);
            glEnable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, -2000000f);
            render(event);
            glDisable(GL_POLYGON_OFFSET_FILL);
            glDisable(GL_BLEND);
            glPolygonOffset(1.0f, 2000000f);
            GlStateManager.popMatrix();
            module.galaxyShader.unbind();
            glPopMatrix();
            glPopAttrib();
        }

        if (module.mode.getValue() == ChamsMode.WaterShader
                && !ESP.isRendering
                && module.waterShader != null)
        {
            if (!module.isValid(event.getEntity())) return;
            event.setCancelled(true);
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glPushMatrix();
            Color color = module.getVisibleColor(event.getEntity());
            module.waterShader.bind();
            module.waterShader.set("time", (System.currentTimeMillis() - module.initTime) / 2000.0f);
            module.waterShader.set("resolution", new Vec2f((mc.displayWidth * 2) /*/ 20.0f*/, (mc.displayHeight * 2) /*/ 20.0f*/));
            module.waterShader.set("tex", 0);

            GlStateManager.pushMatrix();
            // glDepthMask(false);
            // glDisable(GL_DEPTH_TEST);
            GlStateManager.color(1.0f, 1.0f, 1.0f, color.getAlpha() / 255.0f);
            module.waterShader.set("alpha", color.getAlpha() / 255.0f);
            glEnable(GL_POLYGON_OFFSET_FILL);
            glEnable(GL_BLEND);
            glPolygonOffset(1.0f, -2000000f);
            render(event);
            glDisable(GL_BLEND);
            glDisable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, 2000000f);
            // glDepthMask(true);
            // glEnable(GL_DEPTH_TEST);
            GlStateManager.popMatrix();
            module.waterShader.unbind();
            glPopMatrix();
            glPopAttrib();
        }

        if (module.mode.getValue() == ChamsMode.CustomShader
            && !ESP.isRendering
            && module.customShader != null)
        {
            if (!module.isValid(event.getEntity())) return;
            event.setCancelled(true);
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glPushMatrix();
            Color color = module.getVisibleColor(event.getEntity());
            module.customShader.bind();
            module.customShader.set("time", (System.currentTimeMillis() - module.initTime) / 2000.0f);
            module.customShader.set("resolution", new Vec2f((mc.displayWidth * 2) /*/ 20.0f*/, (mc.displayHeight * 2) /*/ 20.0f*/));
            module.customShader.set("tex", 0);

            GlStateManager.pushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, color.getAlpha() / 255.0f);
            module.customShader.set("alpha", color.getAlpha() / 255.0f);
            glEnable(GL_POLYGON_OFFSET_FILL);
            glEnable(GL_BLEND);
            glPolygonOffset(1.0f, -2000000f);
            render(event);
            glDisable(GL_BLEND);
            glDisable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, 2000000f);
            GlStateManager.popMatrix();
            module.customShader.unbind();
            glPopMatrix();
            glPopAttrib();
        }

        if (false
                && module.isValid(event.getEntity())
                && !module.renderModels)
        {
            event.setCancelled(true); // TODO: maybe fix later for integrated graphics? this was the stenciling stuff btw
        }

        if (module.mode.getValue() == ChamsMode.Image
                && module.isValid(event.getEntity())
                && module.imageShader != null)
        {

            ScaledResolution resolution = new ScaledResolution(mc);
            float[] rect = Render2DUtil.getOnScreen2DHitBox(event.getEntity(), resolution.getScaledWidth(), resolution.getScaledHeight());

            glPushMatrix();
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            event.setCancelled(true);
            module.imageShader.bind();
            int currentTexture = glGetInteger(GL_TEXTURE_BINDING_2D);

            // Chams.gif.updateNoDraw();
            // Chams.gif.getCurrentFrame().getTexture().bind();
            // module.gif.getCurrentFrame().bind();

            // mc.getTextureManager().bindTexture(Chams.GALAXY_LOCATION);

            // glBindTexture(GL_TEXTURE_2D, Chams.gif.getCurrentFrame()/*.getFlippedCopy(false, true)*/.getTexture().getTextureID());
            // glBindTexture(GL_TEXTURE_2D, mc.getTextureManager().getTexture(Chams.GALAXY_LOCATION).getGlTextureId());

            module.imageShader.set("sampler", 0);
            GL13.glActiveTexture(GL13.GL_TEXTURE6);

            if (module.gif)
            {

            }
            else
            {
                if (module.dynamicTexture != null)
                {
                    glBindTexture(GL_TEXTURE_2D, module.dynamicTexture.getGlTextureId());
                }
            }
            module.imageShader.set("overlaySampler", 6);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            module.imageShader.set("mixFactor", module.mixFactor.getValue());
            module.imageShader.set("alpha", module.color.getValue().getAlpha() / 255.0f);
            // module.imageShader.set("dimensions", new Vec2f(mc.displayWidth, mc.displayHeight));
            Vec3d gl_FragCoord = new Vec3d(1920, 1080, 0);
            Vector4f imageDimensions = new Vector4f(0, 0, 1920, 1080);
            Vec2d d = new Vec2d(((gl_FragCoord.x - imageDimensions.x) / imageDimensions.z), ((gl_FragCoord.y - imageDimensions.y) / imageDimensions.w));
            // System.out.println(d.getX() + " " + d.getY());

            rect = null;
            if (rect != null)
            {
                // TODO: replace with scaled resolution
                rect[0] = MathHelper.clamp(rect[0], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[1] = MathHelper.clamp(rect[1], 0, mc.displayHeight); // min and max frag coords y-wise
                rect[2] = MathHelper.clamp(rect[2], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[3] = MathHelper.clamp(rect[3], 0, mc.displayHeight); // min and max frag coords y-wise
                // module.imageShader.set("imageDimensions", new Vector4f(rect[0], rect[1], rect[2] - rect[0], rect[3] - rect[1]));
                module.imageShader.set("imageX", rect[2]);
                module.imageShader.set("imageY", rect[3]);
                module.imageShader.set("imageWidth", (rect[0] - rect[2]));
                module.imageShader.set("imageHeight", (rect[1] - rect[3]));
            }
            else
            {
                // module.imageShader.set("imageDimensions", new Vector4f(0.0f, 0.0f, mc.displayHeight, mc.displayWidth));
                module.imageShader.set("imageX", 0.0f);
                module.imageShader.set("imageY", 0.0f);
                module.imageShader.set("imageWidth", (float) mc.displayWidth);
                module.imageShader.set("imageHeight", (float) mc.displayHeight);
            }
            // GL13.glActiveTexture(GL13.GL_TEXTURE0);
            // glBindTexture(GL_TEXTURE_2D, currentTexture);
            boolean shadows = mc.gameSettings.entityShadows;
            mc.gameSettings.entityShadows = false;
            module.renderLayers = false;
            // glDisable(GL_TEXTURE_2D);
            render(event);
            module.renderLayers = true;
            module.imageShader.unbind();
            mc.gameSettings.entityShadows = shadows;
            glPopAttrib();
            glPopMatrix();
        }
    }

    private void render(ModelRenderEvent.Pre event) {
        event.getModel().render(event.getEntity(),
                event.getLimbSwing(),
                event.getLimbSwingAmount(),
                event.getAgeInTicks(),
                event.getNetHeadYaw(),
                event.getHeadPitch(),
                event.getScale());
    }

    private float getFOVModifier(float partialTicks, boolean useFOVSetting)
    {
        if (((IEntityRenderer) mc.entityRenderer).isDebugView())
        {
            return 90.0F;
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            float f = 70.0F;

            if (useFOVSetting)
            {
                f = this.mc.gameSettings.fovSetting;
                f = f * (((IEntityRenderer) mc.entityRenderer).getFovModifierHandPrev() + (((IEntityRenderer) mc.entityRenderer).getFovModifierHand() - ((IEntityRenderer) mc.entityRenderer).getFovModifierHandPrev()) * partialTicks);
            }

            if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHealth() <= 0.0F)
            {
                float f1 = (float)((EntityLivingBase)entity).deathTime + partialTicks;
                f /= (1.0F - 500.0F / (f1 + 500.0F)) * 2.0F + 1.0F;
            }

            IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);

            if (iblockstate.getMaterial() == Material.WATER)
            {
                f = f * 60.0F / 70.0F;
            }

            return f;
        }
    }

}
