package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

final class ListenerRender extends ModuleListener<LogoutSpots, Render3DEvent>
{
    public ListenerRender(LogoutSpots module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (module.render.getValue())
        {
            for (LogoutSpot spot : module.spots.values())
            {
                AxisAlignedBB bb = Interpolation.interpolateAxis(spot.getBoundingBox());
                RenderUtil.startRender();
                RenderUtil.drawOutline(bb, 1.5f, Color.RED);
                RenderUtil.endRender();

                String text = TextColor.RED
                        + spot.getName()
                        + " XYZ : "
                        + MathUtil.round(spot.getX(), 1)
                        + ", "
                        + MathUtil.round(spot.getY(), 1)
                        + ", "
                        + MathUtil.round(spot.getZ(), 1)
                        + " ("
                        + MathUtil.round(spot.getDistance(), 1)
                        + ")";

                RenderUtil.drawNametag(text, bb, module.scale.getValue(), 0xffff0000);
            }
        }
    }

}
