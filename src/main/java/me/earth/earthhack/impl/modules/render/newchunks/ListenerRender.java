package me.earth.earthhack.impl.modules.render.newchunks;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.newchunks.util.ChunkData;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

final class ListenerRender extends ModuleListener<NewChunks, Render3DEvent>
{
    public ListenerRender(NewChunks module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    @SuppressWarnings("DuplicatedCode") //TODO: maybe a glManager class?
    public void invoke(Render3DEvent event)
    {
        boolean lightning = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean blend     = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean texture   = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean depth     = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean lines     = GL11.glIsEnabled(GL11.GL_LINE_SMOOTH);

        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        if (lightning)
        {
            GL11.glDisable(GL11.GL_LIGHTING);
        }

        GL11.glBlendFunc(770, 771);
        if (!blend)
        {
            GL11.glEnable(GL11.GL_BLEND);
        }

        GL11.glLineWidth(0.5f);
        if (texture)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        if (depth)
        {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        if (!lines)
        {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
        }

        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

        Frustum frustum = Interpolation.createFrustum(RenderUtil.getEntity());

        for (ChunkData data : module.data)
        {
            double dX = data.getX() * 16;
            double dZ = data.getZ() * 16;

            AxisAlignedBB bb =
                    new AxisAlignedBB(dX, 0, dZ, dX + 16, 0, dZ + 16);

            if (frustum.isBoundingBoxInFrustum(bb))
            {
                RenderUtil.doPosition(Interpolation.offsetRenderPos(bb));
            }
        }

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        if (!lines)
        {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        if (depth)
        {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        if (texture)
        {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        if (!blend)
        {
            GL11.glDisable(GL11.GL_BLEND);
        }

        if (lightning)
        {
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

}
