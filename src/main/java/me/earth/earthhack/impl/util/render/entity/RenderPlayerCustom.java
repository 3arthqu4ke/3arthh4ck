package me.earth.earthhack.impl.util.render.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

// This allows us to save on memory when using modules that don't need to use normal or texture vertices like chams
// If a new chams mode is implemented that uses these normal/texture vertices, this will have to be changed
// Also lets us add cool effects like gradients for chams without wacky mixins/asm and reduces glEnable calls.
public class RenderPlayerCustom extends RenderLivingBase<AbstractClientPlayer>
{

    private IRenderable modelRenderer;
    private final ArrayList<LayerRenderer<AbstractClientPlayer>> renderers = new ArrayList<>();

    protected RenderPlayerCustom(RenderManager renderManager, CustomModelRenderer renderer)
    {
        super(renderManager, null, 0.0f);
    }

    public void setModelRenderer(IRenderable modelRenderer)
    {
        this.modelRenderer = modelRenderer;
    }

    @Override
    public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        glPushMatrix();
        glPushAttrib(GL_ALL_ATTRIB_BITS);

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);

        modelRenderer.render(partialTicks);

        glPopAttrib();

        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glEnable(GL_BLEND);
        glDisable(GL_LIGHTING);

        for (LayerRenderer<AbstractClientPlayer> renderer : renderers)
        {
            // renderer.doRenderLayer(entity, entity.limbSwing, entity.limbSwingAmount, partialTicks, entity.ticksExisted, entity.rotationYawHead, entity.rotationPitch, 0.6);
        }

        glPopAttrib();

        glPopMatrix();
    }

    // will always return null in this case because we are not even rendering textures in the first place
    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(AbstractClientPlayer entity)
    {
        return null;
    }

    @Override
    protected boolean setBrightness(AbstractClientPlayer entitylivingbaseIn, float partialTicks, boolean combineTextures)
    {
        return false;
    }

    @Override
    public void unsetBrightness() {}

}
