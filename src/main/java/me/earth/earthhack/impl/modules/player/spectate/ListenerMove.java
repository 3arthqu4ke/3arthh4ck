package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.util.math.AxisAlignedBB;

final class ListenerMove extends ModuleListener<Spectate, MoveEvent>
{
    public ListenerMove(Spectate module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (module.stopMove.getValue())
        {
            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();

            // If we are standing on a block the event.y is slightly smaller
            // than 0. If the Block would be removed we would fall a bit which
            // this prevents.
            if (y != 0.0)
            {
                for (AxisAlignedBB a :
                        mc.world.getCollisionBoxes(
                              mc.player,
                              mc.player.getEntityBoundingBox().expand(x, y, z)))
                {
                    y = a.calculateYOffset(mc.player.getEntityBoundingBox(), y);
                }
            }

            event.setX(0);
            event.setY(y == 0.0 ? -0.0784000015258789 : 0);
            event.setZ(0);
        }
    }

}
