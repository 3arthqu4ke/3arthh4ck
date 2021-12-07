package me.earth.earthhack.impl.modules.render.tracers;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.modules.render.tracers.mode.BodyPart;
import me.earth.earthhack.impl.modules.render.tracers.mode.TracerMode;
import me.earth.earthhack.impl.util.render.ColorHelper;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

final class ListenerRender extends ModuleListener<Tracers, Render3DEvent>
{
    private static final ModuleCache<ESP> ESP = Caches.getModule(ESP.class);

    public ListenerRender(Tracers module)
    {
        super(module, Render3DEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        Entity renderEntity = mc.getRenderViewEntity() == null ? mc.player : mc.getRenderViewEntity();
        int i = 0;
        for (Entity entity : module.sorted)
        {
            if (i >= module.tracers.getValue())
            {
                break;
            }

            if (module.isValid(entity))
            {
                Vec3d interpolation = Interpolation.interpolateEntity(entity);
                double x = interpolation.x;
                double y = interpolation.y;
                double z = interpolation.z;

                AxisAlignedBB bb;
                if (module.target.getValue() == BodyPart.Head)
                {
                    bb = new AxisAlignedBB(x - 0.25, y + entity.height - 0.45, z - 0.25, x + 0.25, y + entity.height + 0.055, z + 0.25);
                }
                else
                {
                    bb = new AxisAlignedBB(x - 0.4, y, z - 0.4, x + 0.4, y + entity.height + 0.18, z + 0.4);
                }

                RenderUtil.startRender();
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();

                if (entity instanceof EntityPlayer && Managers.FRIENDS.contains(entity.getName()))
                {
                    GL11.glColor4f(0.33333334f, 0.78431374f, 0.78431374f, 0.55f);
                }
                else
                {
                    float distance = renderEntity.getDistance(entity);

                    float red;
                    if (distance >= 60.0f)
                    {
                        red = 120.0f;
                    }
                    else
                    {
                        red = distance + distance;
                    }

                    Color color = ColorHelper.toColor(red, 100.0f, 50.0f, 0.55f);
                    GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
                }

                final boolean viewBobbing = mc.gameSettings.viewBobbing;
                mc.gameSettings.viewBobbing = false;
                ((IEntityRenderer) mc.entityRenderer).invokeOrientCamera(event.getPartialTicks());
                mc.gameSettings.viewBobbing = viewBobbing;
                GL11.glLineWidth(module.lineWidth.getValue());
                final Vec3d rotateYaw = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-(float) Math.toRadians(renderEntity.rotationPitch)).rotateYaw(-(float) Math.toRadians(renderEntity.rotationYaw));
                GL11.glBegin(GL11.GL_LINES);

                if (module.mode.getValue() == TracerMode.Stem && !ESP.isEnabled())
                {
                    GL11.glVertex3d(x, y, z);
                    GL11.glVertex3d(x, renderEntity.getEyeHeight() + y, z);
                }

                if (module.lines.getValue())
                {
                    GL11.glVertex3d(rotateYaw.x, renderEntity.getEyeHeight() + rotateYaw.y, rotateYaw.z);
                    switch (module.target.getValue())
                    {
                        case Head:
                            GL11.glVertex3d(x, y + entity.height - 0.18, z);
                            break;
                        case Body:
                            GL11.glVertex3d(x, y + entity.height / 2.0f, z);
                            break;
                        case Feet:
                            GL11.glVertex3d(x, y, z);
                            break;
                    }
                }

                GL11.glEnd();
                GL11.glTranslated(x, y, z);
                GL11.glTranslated(-x, -y, -z);

                switch (module.mode.getValue())
                {
                    case Outline:
                    {
                        RenderUtil.doPosition(bb);
                        break;
                    }
                    case Fill:
                    {
                        RenderUtil.fillBox(bb);
                        break;
                    }
                }

                GlStateManager.popMatrix();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                RenderUtil.endRender();
                i++;
            }
        }
    }

}
