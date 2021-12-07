package me.earth.earthhack.impl.modules.render.waypoints;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointRender;
import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointType;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

final class ListenerRender extends ModuleListener<WayPoints, Render3DEvent>
{
    public ListenerRender(WayPoints module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        WayPointRender render = module.render.getValue();
        if (render == WayPointRender.None)
        {
            return;
        }

        WayPointType type =
            WayPointType.fromDimension(mc.world.provider.getDimensionType());

        Entity entity = RenderUtil.getEntity();
        for (WayPointSetting setting : module.getWayPoints())
        {
            double multiplier = 1.0;
            if (setting.getType() == type
                || type == WayPointType.OVW
                    && setting.getType() == WayPointType.Nether
                    && module.ovwInNether.getValue()
                    && assign(multiplier = 8.0)
                || type == WayPointType.Nether
                    && setting.getType() == WayPointType.OVW
                    && module.netherOvw.getValue()
                    && assign(multiplier = 0.125))
            {
                BlockPos pos = setting.getValue();
                double x = Math.floor(pos.getX() * multiplier);
                double y = pos.getY();
                double z = Math.floor(pos.getZ() * multiplier);

                double distanceSq = entity.getDistanceSq(x, y, z);
                if (distanceSq > MathUtil.squareToLong(module.range.getValue()))
                {
                    continue;
                }

                AxisAlignedBB bb =
                        Interpolation.interpolateAxis(
                                new AxisAlignedBB(x, y, z, x+1, y+1, z+1));

                Color c = module.getColor(setting.getType());

                RenderUtil.startRender();
                RenderUtil.drawOutline(bb, 1.5f, c);
                RenderUtil.endRender();

                StringBuilder builder = new StringBuilder();
                builder.append(setting.getName()).append(" ");

                switch (render)
                {
                    case Distance:
                        appendDistance(builder, Math.sqrt(distanceSq));
                        break;
                    case Coordinates:
                        appendCoordinates(builder, pos);
                        break;
                    case Both:
                        appendCoordinates(builder, pos);
                        builder.append(" ");
                        appendDistance(builder, Math.sqrt(distanceSq));
                        break;
                    default:
                }

                RenderUtil.drawNametag(builder.toString(),
                                       bb,
                                       module.scale.getValue(),
                                       c.getRGB());
            }
        }
    }

    @SuppressWarnings("unused")
    private boolean assign(double assignment)
    {
        return true;
    }

    private void appendCoordinates(StringBuilder builder, BlockPos pos)
    {
        builder.append("XYZ: ")
               .append(pos.getX())
               .append(", ")
               .append(pos.getY())
               .append(", ")
               .append(pos.getZ());
    }

    private void appendDistance(StringBuilder builder, double distance)
    {
        builder.append("(")
               .append(MathUtil.round(distance, 1))
               .append(")");
    }

}
