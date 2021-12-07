package me.earth.earthhack.impl.modules.misc.nuker;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

import java.util.Set;

final class ListenerRender extends ModuleListener<Nuker, Render3DEvent>
{
    public ListenerRender(Nuker module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (!module.render.getValue() || !module.nuke.getValue())
        {
            return;
        }

        RayTraceResult result = mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            Set<BlockPos> positions = module.getSelection(result.getBlockPos());

            if (!positions.isEmpty())
            {
                GL11.glPushMatrix();
                GL11.glPushAttrib(1048575);
            }

            for (BlockPos pos : positions)
            {
                AxisAlignedBB bb = Interpolation.interpolatePos(pos, 1.0f);
                RenderUtil.startRender();
                RenderUtil.drawBox(bb, module.color.getValue());
                RenderUtil.endRender();
            }

            if (!positions.isEmpty())
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    }

}
