package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

final class ListenerRender extends ModuleListener<Search, Render3DEvent>
{
    public ListenerRender(Search module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    @SuppressWarnings("CommentedOutCode")
    public void invoke(Render3DEvent event)
    {
        int count = 0;
        module.found = 0;
        boolean inRange = module.countInRange.getValue();
        boolean colored = module.coloredTracers.getValue();
        double distance = MathUtil.square(module.range.getValue());

        Entity renderEntity = RenderUtil.getEntity();
        Frustum frustum = Interpolation.createFrustum(renderEntity);

        for (SearchResult result : module.toRender.values())
        {
            if (!inRange)
            {
                module.found++;
            }

            BlockPos pos = result.getPos();
            if (renderEntity.getDistanceSq(pos) <= distance)
            {
                if (inRange)
                {
                    module.found++;
                }

                if (++count > module.maxBlocks.getValue())
                {
                    continue;
                }

                // TODO: Update State all few Seconds?
                // TODO: the BB could be cached beforehand?
                /*IBlockState state = mc.world.getBlockState(pos);
                int stateColor = module.getColor(state);
                float red   = (float)(stateColor >> 24 & 255) / 255.0f;
                float green = (float)(stateColor >> 16 & 255) / 255.0f;
                float blue  = (float)(stateColor >> 8 & 255)  / 255.0f;
                float alpha = (float)(stateColor & 255)       / 255.0f;
                Color color = new Color(red, green, blue, alpha);*/
                float red   = result.getRed();
                float green = result.getGreen();
                float blue  = result.getBlue();
                float alpha = result.getAlpha();
                Color color = result.getColor();

                if (module.lines.getValue() || module.fill.getValue())
                {
                    AxisAlignedBB bb = result.getBb();
                    /*AxisAlignedBB bb = state.getBoundingBox(mc.world, pos)
                                            .offset(pos.getX(),
                                                    pos.getY(),
                                                    pos.getZ());*/

                    if (frustum.isBoundingBoxInFrustum(bb))
                    {
                        AxisAlignedBB box = Interpolation.offsetRenderPos(bb);

                        if (module.lines.getValue())
                        {
                            RenderUtil.startRender();
                            RenderUtil.drawOutline(box, 1.5f, color);
                            RenderUtil.endRender();
                        }

                        if (module.fill.getValue())
                        {
                            RenderUtil.startRender();
                            RenderUtil.drawBox(box, color);
                            RenderUtil.endRender();
                        }
                    }
                }

                if (module.tracers.getValue())
                {
                    double x = pos.getX() - Interpolation.getRenderPosX();
                    double y = pos.getY() - Interpolation.getRenderPosY();
                    double z = pos.getZ() - Interpolation.getRenderPosZ();

                    if (colored)
                    {
                        GL11.glColor4f(red, green, blue, alpha);
                    }
                    else
                    {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    }

                    RenderUtil.startRender();
                    GL11.glLoadIdentity();
                    GL11.glLineWidth(1.5f);
                    final boolean viewBobbing = mc.gameSettings.viewBobbing;
                    mc.gameSettings.viewBobbing = false;
                    ((IEntityRenderer) mc.entityRenderer)
                            .invokeOrientCamera(event.getPartialTicks());
                    mc.gameSettings.viewBobbing = viewBobbing;

                    Vec3d vec3d = (new Vec3d(0.0, 0.0, 1.0))
                            .rotatePitch(-((float)
                                    Math.toRadians(renderEntity.rotationPitch)))
                            .rotateYaw(-((float)
                                    Math.toRadians(renderEntity.rotationYaw)));

                    GL11.glBegin(GL11.GL_LINES);

                    GL11.glVertex3d(vec3d.x,
                                    renderEntity.getEyeHeight() + vec3d.y,
                                    vec3d.z);

                    GL11.glVertex3d(x + 0.5, y + 0.5, z + 0.5);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glEnd();

                    RenderUtil.endRender();
                }
            }
        }

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

}
