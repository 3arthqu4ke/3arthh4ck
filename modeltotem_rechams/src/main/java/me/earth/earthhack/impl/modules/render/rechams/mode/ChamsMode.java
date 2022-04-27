package me.earth.earthhack.impl.modules.render.rechams.mode;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.core.mixins.render.entity.IRenderEnderCrystal;
import me.earth.earthhack.impl.event.events.render.*;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.rechams.ReChams;
import me.earth.earthhack.impl.util.minecraft.EntityType;
import me.earth.earthhack.impl.util.render.OutlineUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.shader.FramebufferWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public enum ChamsMode implements Globals
{

    None,
    Normal,
    Better() {
        @Override
        public void renderPre(ModelRenderEvent.Pre event, ReChams module)
        {
            event.setCancelled(true);
            event.setCancelled(true);
            Color color = module.getColor(event.getEntity());
            Color wallsColor = module.getWallsColor(event.getEntity());
            glPushMatrix();
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glDisable(GL_ALPHA_TEST);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);
            glLineWidth(1.5f);
            glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_STENCIL_TEST);
            glEnable(GL_POLYGON_OFFSET_LINE);
            glDepthMask(false);
            glDisable(GL_DEPTH_TEST);
            glColor4f(wallsColor.getRed() / 255.0f, wallsColor.getGreen() / 255.0f, wallsColor.getBlue() / 255.0f, wallsColor.getAlpha() / 255.0f);
            render(event);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            if (module.shouldXQZ(event.getEntity())) {
                glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
                render(event);
            }
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glEnable(GL_ALPHA_TEST);
            glPopAttrib();
            glPopMatrix();

            if (module.shouldGlint(event.getEntity()))
            {
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glClearStencil(0);
                glClear(GL_STENCIL_BUFFER_BIT);
                glEnable(GL_STENCIL_TEST);
                glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
                glStencilFunc(GL_ALWAYS, 1, 0xFF);
                glStencilMask(0xFF);
                glEnable(GL_DEPTH_TEST);
                glDepthMask(true);
                // glDisable(GL_ALPHA_TEST);
                // glColor4f(1.0f, 1.0f, 1.0f, 0.0f);
                render(event);
                glDisable(GL_DEPTH_TEST);
                glDepthMask(false);
                glStencilMask(0x00);
                glStencilFunc(GL_EQUAL, 1, 0xFF);
                ChamsMode.renderGlint(event, module, module.getGlintColor(event.getEntity()));
                glStencilFunc(GL_EQUAL, 0, 0xFF);
                ChamsMode.renderGlint(event, module, module.getGlintWallsColor(event.getEntity()));
                glDisable(GL_STENCIL_TEST);
                glPopAttrib();
            }

            if (module.shouldLightning(event.getEntity()))
            {
                ChamsMode.renderLightning(event, module);
            }
        }
    },
    JelloBottom {
        @Override
        public void renderPre(ModelRenderEvent.Pre event, ReChams module)
        {
            event.setCancelled(true);
            render(event);
            Color color = module.getColor(event.getEntity());
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
            glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            render(event);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glEnable(GL_ALPHA_TEST);
            glPopAttrib();
            glPopMatrix();
            if (module.shouldGlint(event.getEntity()))
            {
                // ChamsMode.renderGlint(event, module);
            }

            if (module.shouldLightning(event.getEntity()))
            {
                ChamsMode.renderLightning(event, module);
            }
        }
    },
    JelloTop {
        @Override
        public void renderPost(ModelRenderEvent.Post event, ReChams module)
        {
            Color color = module.getColor(event.getEntity());
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
            glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            render(event);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_LIGHTING);
            glDisable(GL_BLEND);
            glEnable(GL_ALPHA_TEST);
            glPopAttrib();
            glPopMatrix();
            event.setCancelled(true);
            render(event);
            if (module.shouldGlint(event.getEntity()))
            {
                // ChamsMode.renderGlint(event, module);
            }

            if (module.shouldLightning(event.getEntity()))
            {
                ChamsMode.renderLightning(event, module);
            }
            /*renderLightning(event);
            renderEnchantGlint(event);*/
        }
    },
    Shader
    {

        @Override
        public void renderPre(ModelRenderEvent.Pre event, ReChams module)
        {

        }

        @Override
        public void renderCrystalPre(CrystalRenderEvent.Pre event, ReChams module)
        {

        }

        @Override
        public void beginRender(BeginRenderEvent event, ReChams module)
        {
            OutlineUtil.checkSetupFBO();
            glClear(GL_STENCIL_BUFFER_BIT);
            glClearStencil(0);
        }

    },
    FramebufferImage {
        @Override
        public void renderEntity(RenderEntityEvent.Pre event, ReChams module)
        {
            if (module.isValid(event.getEntity(), this) && !module.forceRenderEntities)
            {
                event.setCancelled(true);
                glPushAttrib(GL_ALL_ATTRIB_BITS);

                if (module.shouldAlphaTest(event.getEntity()))
                {
                    glEnable(GL_ALPHA_TEST);
                }

                glEnable(GL_DEPTH_TEST);
                glDepthMask(true);

                if (module.shouldXQZ(event.getEntity()))
                {
                    glEnable(GL_STENCIL_TEST);
                    glStencilOp(GL_KEEP, GL_REPLACE, GL_KEEP);
                    // glStencilFunc(GL_ALWAYS, getWallsMaskFromEntity(event.getEntity()), 0xFF);
                    glStencilFunc(GL_NOTEQUAL, getMaskFromEntity(event.getEntity()), 0xFF);
                    glStencilMask(getWallsMaskFromEntity(event.getEntity()));
                    render(event);
                }

                glEnable(GL_STENCIL_TEST);
                glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
                glStencilFunc(GL_ALWAYS, getMaskFromEntity(event.getEntity()), 0xFF);
                glStencilMask(0xFF);
                render(event);
                glStencilMask(0x00);
                glPopAttrib();
            }
            /*if (!module.forceRenderEntities && module.isValid(event.getEntity(), this)) event.setCancelled(true);*/
            // mc.getRenderManager().renderEntityStatic(event.getEntity(), mc.getRenderPartialTicks(), true);
        }

        @Override
        public void renderEntityPost(RenderEntityEvent.Post event, ReChams module)
        {

        }

        @Override
        public void renderWorld(WorldRenderEvent event, ReChams module)
        {
        }

        @Override
        public void renderHud(PreRenderHandEvent event, ReChams module)
        {

        }

        @Override
        public void render3D(Render3DEvent event, ReChams module)
        {
            // mc.entityRenderer.setupOverlayRendering();
            for (Tuple<ChamsPage, FramebufferWrapper> toUpdate : module.getFramebuffersFromMode(this)) toUpdate.getSecond().updateFramebuffer();
            for (Entity entity : mc.world.loadedEntityList)
            {
                if (module.isValid(entity, this))
                {
                    FramebufferWrapper wrapper = module.getFrameBufferFromEntity(entity).getSecond();
                    boolean renderShadows = mc.gameSettings.entityShadows;
                    mc.gameSettings.entityShadows = false;
                    module.forceRenderEntities = true;
                    wrapper.renderToFramebuffer(() ->
                    {
                        ((IEntityRenderer) mc.entityRenderer).invokeSetupCameraTransform(mc.getRenderPartialTicks(), 0);
                        // GlStateManager.enableLighting();
                        // mc.entityRenderer.enableLightmap();
                        GlStateManager.enableColorMaterial();
                        RenderHelper.enableStandardItemLighting();
                        GlStateManager.enableDepth();
                        GlStateManager.enableAlpha();
                        GlStateManager.depthMask(true);
                        mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), true);
                        GlStateManager.depthMask(false);
                        GlStateManager.disableAlpha();
                        GlStateManager.disableDepth();
                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.disableColorMaterial();
                        // mc.entityRenderer.disableLightmap();
                    });
                    module.forceRenderEntities = false;
                    mc.gameSettings.entityShadows = renderShadows;
                }
            }
            GlStateManager.pushAttrib();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);

            glEnable(GL_STENCIL_TEST);
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
            glStencilMask(0x00);
            for (Tuple<ChamsPage, FramebufferWrapper> framebuffer : module.getFramebuffersFromMode(this))
            {
                // pass 1 - render visible
                glStencilFunc(GL_EQUAL, getMaskFromEntityType(framebuffer.getFirst()), 0xFF);
                module.framebufferImageShader.bind();
                module.framebufferImageShader.set("colorMixFactor", module.colorMixFactor.getValue());
                module.framebufferImageShader.set("mixFactor", module.crystalMixFactor.getValue());
                Color color = module.crystalColor.getValue();
                module.framebufferImageShader.set("inputColor", new org.joml.Vector4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f));
                int id = module.shouldGif(framebuffer.getFirst()) ?
                        module.getGif(framebuffer.getFirst()).getDynamicTexture().getGlTextureId() :
                        module.getImage(framebuffer.getFirst()).getTexture().getGlTextureId();

                framebuffer.getSecond().renderFramebuffer(() ->
                {
                    mc.entityRenderer.setupOverlayRendering();
                    module.framebufferImageShader.set("sampler", 0);
                    GL13.glActiveTexture(GL13.GL_TEXTURE8); // can be whatever, preferably not 1
                    GL11.glBindTexture(GL_TEXTURE_2D, id);
                    module.framebufferImageShader.set("overlaySampler", 8);
                    GL13.glActiveTexture(GL13.GL_TEXTURE0);
                });
                module.framebufferImageShader.unbind();

                // pass two - render non-visible
                glStencilFunc(GL_EQUAL, getWallsMaskFromEntityType(framebuffer.getFirst()), 0xFF);
                module.framebufferImageShader.bind();
                module.framebufferImageShader.set("colorMixFactor", module.colorMixFactor.getValue());
                module.framebufferImageShader.set("mixFactor", module.crystalMixFactor.getValue());
                // Color color = module.crystalColor.getValue();
                module.framebufferImageShader.set("inputColor", new org.joml.Vector4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f));
                int wallsId = module.shouldWallsGif(framebuffer.getFirst()) ?
                        module.getWallsGif(framebuffer.getFirst()).getDynamicTexture().getGlTextureId() :
                        module.getWallsImage(framebuffer.getFirst()).getTexture().getGlTextureId();

                framebuffer.getSecond().renderFramebuffer(() ->
                {
                    mc.entityRenderer.setupOverlayRendering();
                    module.framebufferImageShader.set("sampler", 0);
                    GL13.glActiveTexture(GL13.GL_TEXTURE8); // can be whatever, preferably not 1
                    GL11.glBindTexture(GL_TEXTURE_2D, wallsId);
                    module.framebufferImageShader.set("overlaySampler", 8);
                    GL13.glActiveTexture(GL13.GL_TEXTURE0);
                });
                module.framebufferImageShader.unbind();
            }

            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.popAttrib();
            ((IEntityRenderer) mc.entityRenderer).invokeSetupCameraTransform(mc.getRenderPartialTicks(), 0);
        }

        @Override
        public void beginRender(BeginRenderEvent event, ReChams module)
        {
            OutlineUtil.checkSetupFBO();
            glClear(GL_STENCIL_BUFFER_BIT);
            glClearStencil(0);
        }

    },
    /**
     * Interesting mode here, can do some cool stuff with framebuffers and shaders this way
     * If the stencil value for the fragment is one, the fragment is visible
     * Otherwise, the fragment is not visible
     *
     * TODO: maybe do the same thing with the entity's bounding box so that we can do this for glow/outline shader esp as well?
     * TODO: move this to mode: image!!!
     */
    FramebufferTest {

        @Override
        public void renderPre(ModelRenderEvent.Pre event, ReChams module)
        {

        }

        @Override
        public void render3D(Render3DEvent event, ReChams module)
        {
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glEnable(GL_STENCIL_TEST);
            glStencilFunc(GL_EQUAL, 4, 0xFF); // only pass if the buffer is not equal to 0
            glStencilMask(0x00); // no longer writing to the stencil buffer
            // reset projection and modelview matrices
            FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
            FloatBuffer modelViewBuffer = GLAllocation.createDirectFloatBuffer(16);
            glGetFloat(GL_PROJECTION_MATRIX, buffer);
            glGetFloat(GL_MODELVIEW_MATRIX, modelViewBuffer);
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            // finally draw texture
            mc.getTextureManager().bindTexture(me.earth.earthhack.impl.modules.render.chams.Chams.GALAXY_LOCATION);
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
            // glDisable(GL_TEXTURE_2D);
            glStencilFunc(GL_EQUAL, 5, 0xFF);
            Render2DUtil.drawRect(0, 0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), Color.GREEN.getRGB());

            glMatrixMode(GL_PROJECTION);
            glLoadMatrix(buffer);
            glMatrixMode(GL_MODELVIEW);
            glPopAttrib();
        }

        @Override
        public void beginRender(BeginRenderEvent event, ReChams module)
        {
            OutlineUtil.checkSetupFBO();
            glClear(GL_STENCIL_BUFFER_BIT);
            glClearStencil(0);
        }

        @Override
        public void renderCrystalPre(CrystalRenderEvent.Pre event, ReChams module)
        {
            // this is needed so that weird clipping does not occur when using the other stenciling method
            event.setCancelled(true);
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glEnable(GL_STENCIL_TEST); // enable stencil testing
            glEnable(GL_ALPHA_TEST);
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);
            glEnable(GL_TEXTURE_2D);
            glStencilOp(GL_KEEP, GL_REPLACE, GL_KEEP);
            glStencilFunc(GL_ALWAYS, 5, 0xFF); // all fragments should pass the stencil test
            glStencilMask(0xFF); // enable writing to the stencil buffer
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // color white
            render(event);
            glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
            glStencilFunc(GL_ALWAYS, 4, 0xFF);
            glStencilMask(0xFF); // only write 5 to stencil buffer
            render(event);
            glPopAttrib();
        }

    },
    ShaderImage {
        @Override
        public void renderPre(ModelRenderEvent.Pre event, ReChams module)
        {
            ScaledResolution resolution = new ScaledResolution(mc);
            float[] rect = Render2DUtil.getOnScreen2DHitBox(event.getEntity(), resolution.getScaledWidth(), resolution.getScaledHeight());

            glPushMatrix();
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            event.setCancelled(true);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_ALPHA_TEST);
            glDisable(GL_LIGHTING);

            if (module.shouldAlphaTest(event.getEntity()))
            {
                glEnable(GL_ALPHA_TEST);
            }

            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);

            if (module.shouldXQZ(event.getEntity()))
            {
                glEnable(GL_STENCIL_TEST);
                glStencilOp(GL_KEEP, GL_REPLACE, GL_KEEP);
                // glStencilFunc(GL_ALWAYS, getWallsMaskFromEntity(event.getEntity()), 0xFF);
                glStencilFunc(GL_NOTEQUAL, getMaskFromEntity(event.getEntity()), 0xFF);
                glStencilMask(getWallsMaskFromEntity(event.getEntity()));
                render(event);
            }

            glEnable(GL_STENCIL_TEST);
            glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
            glStencilFunc(GL_ALWAYS, getMaskFromEntity(event.getEntity()), 0xFF);
            glStencilMask(0xFF);
            render(event);
            glStencilMask(0x00);

            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);

            glStencilOp(GL_KEEP,GL_KEEP, GL_KEEP);
            glStencilFunc(GL_EQUAL, getMaskFromEntity(event.getEntity()), 0xFF);
            module.imageShader.bind();
            module.imageShader.set("sampler", 0);
            GL13.glActiveTexture(GL13.GL_TEXTURE6);

            int id = module.shouldGif(event.getEntity()) ?
                    module.getGif(event.getEntity()).getDynamicTexture().getGlTextureId() :
                    module.getImage(event.getEntity()).getTexture().getGlTextureId();

            GL11.glBindTexture(GL_TEXTURE_2D, id);
            module.imageShader.set("overlaySampler", 6);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            module.imageShader.set("mixFactor", module.getMixFactor(event.getEntity()));
            module.imageShader.set("colorMixFactor", module.getColorMixFactor(event.getEntity()));
            module.imageShader.set("dimensions", new Vec2f(mc.displayWidth, mc.displayHeight));
            // module.imageShader.set("fill", module.shouldFill(event.getEntity()));

            module.imageShader.set("inputColor", module.getColor(event.getEntity()));

            if (!module.shouldFit(event.getEntity())) rect = null;

            if (rect != null)
            {
                // TODO: replace with scaled resolution
                rect[0] = MathHelper.clamp(rect[0], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[1] = MathHelper.clamp(rect[1], 0, mc.displayHeight); // min and max frag coords y-wise
                rect[2] = MathHelper.clamp(rect[2], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[3] = MathHelper.clamp(rect[3], 0, mc.displayHeight); // min and max frag coords y-wise
                module.imageShader.set("imageX", rect[2] * resolution.getScaleFactor());
                module.imageShader.set("imageY", mc.displayHeight - (rect[3] * resolution.getScaleFactor()) - ((rect[1] - rect[3]) * resolution.getScaleFactor()));
                module.imageShader.set("imageWidth", (rect[0] - rect[2]) * resolution.getScaleFactor());
                module.imageShader.set("imageHeight", (rect[1] - rect[3]) * resolution.getScaleFactor());
            }
            else
            {
                module.imageShader.set("imageX", 0.0f);
                module.imageShader.set("imageY", 0.0f);
                module.imageShader.set("imageWidth", (float) mc.displayWidth);
                module.imageShader.set("imageHeight", (float) mc.displayHeight);
            }
            render(event);
            module.imageShader.unbind();

            glStencilFunc(GL_EQUAL, getWallsMaskFromEntity(event.getEntity()), 0xFF);
            module.imageShader.bind();
            module.imageShader.set("sampler", 0);
            GL13.glActiveTexture(GL13.GL_TEXTURE6);

            int wallsId = module.shouldWallsGif(event.getEntity()) ?
                    module.getWallsGif(event.getEntity()).getDynamicTexture().getGlTextureId() :
                    module.getWallsImage(event.getEntity()).getTexture().getGlTextureId();

            GL11.glBindTexture(GL_TEXTURE_2D, wallsId);
            module.imageShader.set("overlaySampler", 6);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            module.imageShader.set("mixFactor", module.getMixFactor(event.getEntity()));
            module.imageShader.set("colorMixFactor", module.getColorMixFactor(event.getEntity()));
            module.imageShader.set("dimensions", new Vec2f(mc.displayWidth, mc.displayHeight));
            // module.imageShader.set("fill", module.shouldFill(event.getEntity()));

            module.imageShader.set("inputColor", module.getColor(event.getEntity()));

            if (!module.shouldFit(event.getEntity())) rect = null;

            if (rect != null)
            {
                // TODO: replace with scaled resolution
                rect[0] = MathHelper.clamp(rect[0], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[1] = MathHelper.clamp(rect[1], 0, mc.displayHeight); // min and max frag coords y-wise
                rect[2] = MathHelper.clamp(rect[2], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[3] = MathHelper.clamp(rect[3], 0, mc.displayHeight); // min and max frag coords y-wise
                module.imageShader.set("imageX", rect[2] * resolution.getScaleFactor());
                module.imageShader.set("imageY", mc.displayHeight - (rect[3] * resolution.getScaleFactor()) - ((rect[1] - rect[3]) * resolution.getScaleFactor()));
                module.imageShader.set("imageWidth", (rect[0] - rect[2]) * resolution.getScaleFactor());
                module.imageShader.set("imageHeight", (rect[1] - rect[3]) * resolution.getScaleFactor());
            }
            else
            {
                module.imageShader.set("imageX", 0.0f);
                module.imageShader.set("imageY", 0.0f);
                module.imageShader.set("imageWidth", (float) mc.displayWidth);
                module.imageShader.set("imageHeight", (float) mc.displayHeight);
            }
            render(event);
            module.imageShader.unbind();

            glEnable(GL_LIGHTING);
            glEnable(GL_ALPHA_TEST);
            glDisable(GL_BLEND);

            glPopAttrib();
            glPopMatrix();
        }

        @Override
        public void beginRender(BeginRenderEvent event, ReChams module)
        {
            OutlineUtil.checkSetupFBO();
            glClear(GL_STENCIL_BUFFER_BIT);
            glClearStencil(0);
        }

        @Override
        public void renderCrystalPre(CrystalRenderEvent.Pre event, ReChams module)
        {
            ScaledResolution resolution = new ScaledResolution(mc);
            float[] rect = Render2DUtil.getOnScreen2DHitBox(event.getEntity(), resolution.getScaledWidth(), resolution.getScaledHeight());

            glPushMatrix();
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            event.setCancelled(true);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_ALPHA_TEST);
            glDisable(GL_LIGHTING);

            if (module.crystalAlphaTest.getValue())
            {
                glEnable(GL_ALPHA_TEST);
            }

            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);

            if (module.crystalXQZ.getValue())
            {
                glEnable(GL_STENCIL_TEST);
                glStencilOp(GL_KEEP, GL_REPLACE, GL_KEEP);
                // glStencilFunc(GL_ALWAYS, getWallsMaskFromEntity(event.getEntity()), 0xFF);
                glStencilFunc(GL_NOTEQUAL, getMaskFromEntity(event.getEntity()), 0xFF);
                glStencilMask(getWallsMaskFromEntity(event.getEntity()));
                render(event);
            }

            glEnable(GL_STENCIL_TEST);
            glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
            glStencilFunc(GL_ALWAYS, getMaskFromEntity(event.getEntity()), 0xFF);
            glStencilMask(0xFF);
            render(event);
            glStencilMask(0x00);

            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);

            glStencilOp(GL_KEEP,GL_KEEP, GL_KEEP);
            glStencilFunc(GL_EQUAL, getMaskFromEntity(event.getEntity()), 0xFF);
            module.imageShader.bind();
            module.imageShader.set("sampler", 0);
            GL13.glActiveTexture(GL13.GL_TEXTURE6);

            int id = module.shouldGif(event.getEntity()) ?
                    module.getGif(event.getEntity()).getDynamicTexture().getGlTextureId() :
                    module.getImage(event.getEntity()).getTexture().getGlTextureId();

            GL11.glBindTexture(GL_TEXTURE_2D, id);
            module.imageShader.set("overlaySampler", 6);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            module.imageShader.set("mixFactor", module.getMixFactor(event.getEntity()));
            module.imageShader.set("colorMixFactor", module.getColorMixFactor(event.getEntity()));
            module.imageShader.set("dimensions", new Vec2f(mc.displayWidth, mc.displayHeight));
            // module.imageShader.set("fill", module.shouldFill(event.getEntity()));

            module.imageShader.set("inputColor", module.getColor(event.getEntity()));

            if (!module.shouldFit(event.getEntity())) rect = null;

            if (rect != null)
            {
                // TODO: replace with scaled resolution
                rect[0] = MathHelper.clamp(rect[0], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[1] = MathHelper.clamp(rect[1], 0, mc.displayHeight); // min and max frag coords y-wise
                rect[2] = MathHelper.clamp(rect[2], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[3] = MathHelper.clamp(rect[3], 0, mc.displayHeight); // min and max frag coords y-wise
                module.imageShader.set("imageX", rect[2] * resolution.getScaleFactor());
                module.imageShader.set("imageY", mc.displayHeight - (rect[3] * resolution.getScaleFactor()) - ((rect[1] - rect[3]) * resolution.getScaleFactor()));
                module.imageShader.set("imageWidth", (rect[0] - rect[2]) * resolution.getScaleFactor());
                module.imageShader.set("imageHeight", (rect[1] - rect[3]) * resolution.getScaleFactor());
            }
            else
            {
                module.imageShader.set("imageX", 0.0f);
                module.imageShader.set("imageY", 0.0f);
                module.imageShader.set("imageWidth", (float) mc.displayWidth);
                module.imageShader.set("imageHeight", (float) mc.displayHeight);
            }
            render(event);
            module.imageShader.unbind();

            glStencilFunc(GL_EQUAL, getWallsMaskFromEntity(event.getEntity()), 0xFF);
            module.imageShader.bind();
            module.imageShader.set("sampler", 0);
            GL13.glActiveTexture(GL13.GL_TEXTURE6);

            int wallsId = module.shouldWallsGif(event.getEntity()) ?
                    module.getWallsGif(event.getEntity()).getDynamicTexture().getGlTextureId() :
                    module.getWallsImage(event.getEntity()).getTexture().getGlTextureId();

            GL11.glBindTexture(GL_TEXTURE_2D, wallsId);
            module.imageShader.set("overlaySampler", 6);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            module.imageShader.set("mixFactor", module.getMixFactor(event.getEntity()));
            module.imageShader.set("colorMixFactor", module.getColorMixFactor(event.getEntity()));
            module.imageShader.set("dimensions", new Vec2f(mc.displayWidth, mc.displayHeight));
            // module.imageShader.set("fill", module.shouldFill(event.getEntity()));

            module.imageShader.set("inputColor", module.getColor(event.getEntity()));

            if (!module.shouldFit(event.getEntity())) rect = null;

            if (rect != null)
            {
                // TODO: replace with scaled resolution
                rect[0] = MathHelper.clamp(rect[0], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[1] = MathHelper.clamp(rect[1], 0, mc.displayHeight); // min and max frag coords y-wise
                rect[2] = MathHelper.clamp(rect[2], 0, mc.displayWidth); // min and max frag coords x-wise
                rect[3] = MathHelper.clamp(rect[3], 0, mc.displayHeight); // min and max frag coords y-wise
                module.imageShader.set("imageX", rect[2] * resolution.getScaleFactor());
                module.imageShader.set("imageY", mc.displayHeight - (rect[3] * resolution.getScaleFactor()) - ((rect[1] - rect[3]) * resolution.getScaleFactor()));
                module.imageShader.set("imageWidth", (rect[0] - rect[2]) * resolution.getScaleFactor());
                module.imageShader.set("imageHeight", (rect[1] - rect[3]) * resolution.getScaleFactor());
            }
            else
            {
                module.imageShader.set("imageX", 0.0f);
                module.imageShader.set("imageY", 0.0f);
                module.imageShader.set("imageWidth", (float) mc.displayWidth);
                module.imageShader.set("imageHeight", (float) mc.displayHeight);
            }
            render(event);
            module.imageShader.unbind();

            glEnable(GL_LIGHTING);
            glEnable(GL_ALPHA_TEST);
            glDisable(GL_BLEND);

            glPopAttrib();
            glPopMatrix();
        }
    },
    Image
    {

        @Override
        public void renderPre(ModelRenderEvent.Pre event, ReChams module)
        {

        }

        @Override
        public void renderCrystalPre(CrystalRenderEvent.Pre event, ReChams module)
        {

        }

        @Override
        public void render3D(Render3DEvent event, ReChams module)
        {

        }

    };

    public void renderPre(ModelRenderEvent.Pre event, ReChams module){}
    public void renderPost(ModelRenderEvent.Post event, ReChams module){}
    public void renderCrystalPre(CrystalRenderEvent.Pre event, ReChams module){}
    public void renderCrystalPost(CrystalRenderEvent.Post event, ReChams module){}
    public void renderEntity(RenderEntityEvent.Pre event, ReChams module) {}
    public void renderWorld(WorldRenderEvent event, ReChams module) {}
    public void renderHud(PreRenderHandEvent event, ReChams module) {}
    public void render3D(Render3DEvent event, ReChams module) {}
    public void renderEntityPost(RenderEntityEvent.Post event, ReChams module) {}
    public void render2D(Render2DEvent event, ReChams module) {}
    public void beginRender(BeginRenderEvent event, ReChams module) {}
    public void renderCrystalCube(RenderCrystalCubeEvent event, ReChams module) {}
    public void renderArmor(RenderArmorEvent event, ReChams module) {}

    private static void render(ModelRenderEvent.Pre event) {
        event.getModel().render(event.getEntity(),
                event.getLimbSwing(),
                event.getLimbSwingAmount(),
                event.getAgeInTicks(),
                event.getNetHeadYaw(),
                event.getHeadPitch(),
                event.getScale());
    }

    private static void render(ModelRenderEvent.Post event) {
        event.getModel().render(event.getEntity(),
                event.getLimbSwing(),
                event.getLimbSwingAmount(),
                event.getAgeInTicks(),
                event.getNetHeadYaw(),
                event.getHeadPitch(),
                event.getScale());
    }

    private static void render(CrystalRenderEvent.Pre event) {
        event.getModel().render(event.getEntity(),
                event.getLimbSwing(),
                event.getLimbSwingAmount(),
                event.getAgeInTicks(),
                event.getNetHeadYaw(),
                event.getHeadPitch(),
                event.getScale());
    }

    private static void render(CrystalRenderEvent.Post event) {
        event.getModel().render(event.getEntity(),
                event.getLimbSwing(),
                event.getLimbSwingAmount(),
                event.getAgeInTicks(),
                event.getNetHeadYaw(),
                event.getHeadPitch(),
                event.getScale());
    }

    private static void render(RenderEntityEvent.Pre event)
    {
        event.getRenderer().doRender(event.getEntity(),
                event.getPosX(),
                event.getPosY(),
                event.getPosZ(),
                event.getEntityYaw(),
                event.getPartialTicks());
    }

    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("earthhack:textures/client/lightning.png");

    // stencil masks
    private static final int CRYSTAL_WALLS = 4;
    private static final int CRYSTAL = 5;

    private static final int PLAYER_WALLS = 6;
    private static final int PLAYER = 7;

    private static final int FRIEND_WALLS = 8;
    private static final int FRIEND = 9;

    private static final int ENEMY_WALLS = 10;
    private static final int ENEMY = 11;

    private static int getMaskFromEntity(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? FRIEND : (Managers.ENEMIES.contains(entity) ? ENEMY : PLAYER)) : (entity instanceof EntityEnderCrystal ? CRYSTAL : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? 0 : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? 0 : 0))));
    }

    private static int getWallsMaskFromEntity(Entity entity)
    {
        return (entity instanceof EntityPlayer ? (Managers.FRIENDS.contains(entity) ? FRIEND_WALLS : (Managers.ENEMIES.contains(entity) ? ENEMY_WALLS : PLAYER_WALLS)) : (entity instanceof EntityEnderCrystal ? CRYSTAL_WALLS : ((EntityType.isAngry(entity) || EntityType.isAnimal(entity)) ? 0 : ((EntityType.isMonster(entity) || EntityType.isBoss(entity)) ? 0 : 0))));
    }

    private static int getMaskFromEntityType(ChamsPage entity)
    {
        switch (entity)
        {
            case Players:
                return PLAYER;
            case Friends:
                return FRIEND;
            case Enemies:
                return ENEMY;
            case Crystals:
                return CRYSTAL;
            default:
                return -1;
        }
    }

    private static int getWallsMaskFromEntityType(ChamsPage entity)
    {
        switch (entity)
        {
            case Players:
                return PLAYER_WALLS;
            case Friends:
                return FRIEND_WALLS;
            case Enemies:
                return ENEMY_WALLS;
            case Crystals:
                return CRYSTAL_WALLS;
            default:
                return -1;
        }
    }

    private static void renderEntities(double renderPosX, double renderPosY, double renderPosZ) {

        for (Entity e : mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderCrystal)) continue;
            if (e == mc.getRenderViewEntity()) continue;
            if (e.ticksExisted == 0) {
                e.lastTickPosX = e.posX;
                e.lastTickPosY = e.posY;
                e.lastTickPosZ = e.posZ;
            }

            double d0 = e.lastTickPosX + (e.posX - e.lastTickPosX) * mc.getRenderPartialTicks();
            double d1 = e.lastTickPosY + (e.posY - e.lastTickPosY) * mc.getRenderPartialTicks();
            double d2 = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * mc.getRenderPartialTicks();
            double f = e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * mc.getRenderPartialTicks();

            // glStencilFunc(GL_ALWAYS, 1, 0xFF);
            mc.getRenderManager().renderEntity(e, d0 - renderPosX, d1 - renderPosY, d2 - renderPosZ, (float) f, mc.getRenderPartialTicks(), true);
        }
    }

    private static void renderWireframe(ModelRenderEvent.Pre event, Color color, boolean xqz, Color wallsColor, float lineWidth)
    {
        glPushMatrix();
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glLineWidth(lineWidth);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_POLYGON_OFFSET_LINE);
        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glColor4f(wallsColor.getRed() / 255.0f, wallsColor.getGreen() / 255.0f, wallsColor.getBlue() / 255.0f, wallsColor.getAlpha() / 255.0f);
        render(event);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST); // redundant
        if (xqz)
        {
            glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            render(event);
        }
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glPopAttrib();
        glPopMatrix();
    }

    private static void renderWireframe(ModelRenderEvent.Post event, ReChams module, Color color, boolean xqz, Color wallsColor, float lineWidth)
    {
        glPushMatrix();
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glLineWidth(lineWidth);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_POLYGON_OFFSET_LINE);
        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glColor4f(wallsColor.getRed() / 255.0f, wallsColor.getGreen() / 255.0f, wallsColor.getBlue() / 255.0f, wallsColor.getAlpha() / 255.0f);
        render(event);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST); // redundant
        if (xqz)
        {
            glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            render(event);
        }
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glPopAttrib();
        glPopMatrix();
    }

    private static void renderGlint(ModelRenderEvent.Pre event, ReChams module, Color color)
    {
        renderEnchantEffect(event.getEntity(), event.getModel(), event.getLimbSwing(), event.getLimbSwingAmount(), mc.getRenderPartialTicks(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale(), 1.0f, color);
    }

    private static void renderGlint(ModelRenderEvent.Post event, ReChams module, Color color)
    {
        renderEnchantEffect(event.getEntity(), event.getModel(), event.getLimbSwing(), event.getLimbSwingAmount(), mc.getRenderPartialTicks(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale(), 1.0f, color);
    }

    private static void renderEnchantEffect(EntityLivingBase p_188364_1_, ModelBase model, float p_188364_3_, float p_188364_4_, float p_188364_5_, float p_188364_6_, float p_188364_7_, float p_188364_8_, float p_188364_9_, float glintScale, Color color)
    {
        float f3 = (float)p_188364_1_.ticksExisted + p_188364_5_;
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        float f4 = MathHelper.sin(f3 * 0.2f) / 2.0f + 0.5f;
        f4 += f4 * f4;
        GL11.glPushAttrib(1048575);
        GL11.glPolygonMode(1032, 6914);
        GL11.glDisable(2896);
        // GL11.glEnable(GL_DEPTH_TEST);
        GL11.glDisable(GL_DEPTH_TEST);
        GL11.glEnable(3042);
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        for (int i = 0; i < 2; ++i)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            final float tScale = 0.33333334f; /** (float)7l.v.6();*/
            // GlStateManager.scale(tScale, tScale, tScale);
            GL11.glScalef(glintScale, glintScale, glintScale);
            GlStateManager.rotate(30.0f - i * 60.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.translate(0.0F, f3 * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
            // GlStateManager.translate(0.0f, (p_188364_1_.ticksExisted + p_188364_5_) * (0.001f + i * 0.003f), 0.0f);
            GlStateManager.matrixMode(5888);
            /*if (module.depth.getValue())
            {
                GL11.glDepthMask(true);
                GL11.glEnable(2929);
            }*/
            model.render(p_188364_1_, p_188364_3_, p_188364_4_, p_188364_6_, p_188364_7_, p_188364_8_, p_188364_9_);
            /*if (module.depth.getValue())
            {
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
            }*/
        }
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        // GL11.glScalef(1.0f / (float)7l.F.6(), 1.0f / (float)7l.F.6(), 1.0f / (float)7l.F.6());
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void renderLightning(ModelRenderEvent.Pre event, ReChams module)
    {

    }

    private static void renderLightning(ModelRenderEvent.Post event, ReChams module)
    {

    }

    private static void renderLightning(ModelBase modelBase, EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, Color color)
    {
        Render<? extends EntityLivingBase> render = mc.getRenderManager().getEntityRenderObject(entitylivingbaseIn);
        RenderLivingBase<?> renderLivingBase = (RenderLivingBase<?>) render;
        assert renderLivingBase != null;
        boolean flag = entitylivingbaseIn.isInvisible();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(!flag);
        mc.getTextureManager().bindTexture(LIGHTNING_TEXTURE);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
        GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        float f1 = 0.5F;
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        modelBase.setModelAttributes(renderLivingBase.getMainModel());
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        modelBase.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(flag);
    }

    private static void renderLightning(ModelBase modelBase, EntityEnderCrystal entityEnderCrystal, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, Color color)
    {
        Render<? extends EntityEnderCrystal> render = mc.getRenderManager().getEntityRenderObject(entityEnderCrystal);
        RenderEnderCrystal renderLivingBase = (RenderEnderCrystal) render;
        assert renderLivingBase != null;
        boolean flag = entityEnderCrystal.isInvisible();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(!flag);
        mc.getTextureManager().bindTexture(LIGHTNING_TEXTURE);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = (float)entityEnderCrystal.ticksExisted + partialTicks;
        GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        float f1 = 0.5F;
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        modelBase.setModelAttributes(((IRenderEnderCrystal) renderLivingBase).getModelEnderCrystal());
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        modelBase.render(entityEnderCrystal, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(flag);
    }

}
