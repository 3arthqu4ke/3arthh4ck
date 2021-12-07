package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

final class ListenerRender extends ModuleListener<LagOMeter, Render3DEvent>
{
    private static final double HEAD_X = -0.2;
    private static final double HEAD_Y = 1.5;
    private static final double HEAD_Z = -0.25;

    private static final double HEAD_X1 = 0.2;
    private static final double HEAD_Y1 = 1.95; // Too big...
    private static final double HEAD_Z1 = 0.25;

    private static final double CHEST_X = -0.18;
    private static final double CHEST_Y = 0.8;
    private static final double CHEST_Z = -0.275;

    private static final double CHEST_X1 = 0.18;
    private static final double CHEST_Y1 = 1.5;
    private static final double CHEST_Z1 = 0.275;

    private static final double ARM1_X = -0.1;
    private static final double ARM1_Y = 0.75;
    private static final double ARM1_Z = 0.275;

    private static final double ARM1_X1 = 0.1;
    private static final double ARM1_Y1 = 1.5;
    private static final double ARM1_Z1 = 0.5;

    private static final double ARM2_X = -0.1;
    private static final double ARM2_Y = 0.75;
    private static final double ARM2_Z = -0.275;

    private static final double ARM2_X1 = 0.1;
    private static final double ARM2_Y1 = 1.5;
    private static final double ARM2_Z1 = -0.5;

    private static final double LEG1_X = -0.15;
    private static final double LEG1_Y = 0.0;
    private static final double LEG1_Z = 0.0;

    private static final double LEG1_X1 = 0.15;
    private static final double LEG1_Y1 = 0.8;
    private static final double LEG1_Z1 = 0.25;

    private static final double LEG2_X = -0.15;
    private static final double LEG2_Y = 0.0;
    private static final double LEG2_Z = 0.0;

    private static final double LEG2_X1 = 0.15;
    private static final double LEG2_Y1 = 0.8;
    private static final double LEG2_Z1 = -0.25;

    public ListenerRender(LagOMeter module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void invoke(Render3DEvent event)
    {
        if (!module.esp.getValue())
        {
            return;
        }

        double factor = 1.0;
        if (module.teleported.get())
        {
            double time = (double) module.time.getValue();
            long t = System.currentTimeMillis() - Managers.NCP.getTimeStamp();
            if (t > time)
            {
                return;
            }

            factor = MathUtil.clamp(1.0 - (t / time), 0.0, 1.0);
        }
        GlStateManager.pushMatrix();
        double x = module.x - mc.getRenderManager().viewerPosX;
        double y = module.y - mc.getRenderManager().viewerPosY;
        double z = module.z - mc.getRenderManager().viewerPosZ;
        float yaw = module.yaw;
        float pitch = module.pitch;

        AxisAlignedBB head = new AxisAlignedBB(
            x + HEAD_X, y + HEAD_Y, z + HEAD_Z,
            x + HEAD_X1, y + HEAD_Y1, z + HEAD_Z1);
        AxisAlignedBB chest = new AxisAlignedBB(
            x + CHEST_X, y + CHEST_Y, z + CHEST_Z,
            x + CHEST_X1, y + CHEST_Y1, z + CHEST_Z1);
        AxisAlignedBB arm1 = new AxisAlignedBB(
            x + ARM1_X, y + ARM1_Y, z + ARM1_Z,
            x + ARM1_X1, y + ARM1_Y1, z + ARM1_Z1);
        AxisAlignedBB arm2 = new AxisAlignedBB(
            x + ARM2_X, y + ARM2_Y, z + ARM2_Z,
            x + ARM2_X1, y + ARM2_Y1, z + ARM2_Z1);
        AxisAlignedBB leg1 = new AxisAlignedBB(
            x + LEG1_X, y + LEG1_Y, z + LEG1_Z,
            x + LEG1_X1, y + LEG1_Y1, z + LEG1_Z1);
        AxisAlignedBB leg2 = new AxisAlignedBB(
            x + LEG2_X, y + LEG2_Y, z + LEG2_Z,
            x + LEG2_X1, y + LEG2_Y1, z + LEG2_Z1);

        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180 + (-(yaw + 90)), 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(-x, -y, -z);

        Color box = getColor(module.color.getValue(), factor);
        Color out = getColor(module.outline.getValue(), factor);

        renderAxis(chest, box, out);
        renderAxis(arm1, box, out);
        renderAxis(arm2, box, out);
        renderAxis(leg1, box, out);
        renderAxis(leg2, box, out);

        GlStateManager.translate(x, y + 1.5, z);
        GlStateManager.rotate(pitch, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(-x, -y - 1.5, -z);

        renderAxis(head, box, out);

        GlStateManager.translate(x, y + 1.5, z);
        GlStateManager.rotate(-pitch, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(-x, -y - 1.5, -z);

        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180 + (yaw + 90), 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(-x, -y, -z);

        if (module.nametag.getValue())
        {
            int color = module.textColor.getRGB();
            double scale = module.scale.getValue();
            RenderUtil.drawNametag("Lag", x, y + 0.7, z, scale, color, false);
        }
        GlStateManager.popMatrix();
    }

    private void renderAxis(AxisAlignedBB bb, Color color, Color outline)
    {
        RenderUtil.renderBox(bb, color, outline, module.lineWidth.getValue());
    }

    private Color getColor(Color c, double factor)
    {
        if (factor == 1.0)
        {
            return c;
        }

        return new Color(
                c.getRed(),
                c.getGreen(),
                c.getBlue(),
                (int) MathUtil.clamp(c.getAlpha() * factor, 0, 255));
    }

}
