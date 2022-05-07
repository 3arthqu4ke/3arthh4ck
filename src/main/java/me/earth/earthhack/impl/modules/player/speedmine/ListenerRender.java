package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.mode.ESPMode;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

final class ListenerRender extends ModuleListener<Speedmine, Render3DEvent>
{
    public ListenerRender(Speedmine module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (!PlayerUtil.isCreative(mc.player)
                && module.esp.getValue() != ESPMode.None
                && module.bb != null)
        {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            float max = Math.min(module.maxDamage, 1.0f);
            AxisAlignedBB renderBB = module.bb;
            if (module.growRender.getValue() && max < 1.0f)
            {
                renderBB = renderBB.grow(-0.5 + (max / 2.0));
            }

            AxisAlignedBB bb = Interpolation.interpolateAxis(renderBB);
            module.esp.getValue().drawEsp(module, bb, max);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

}
