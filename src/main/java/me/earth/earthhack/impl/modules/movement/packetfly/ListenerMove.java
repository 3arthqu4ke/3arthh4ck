package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Phase;

final class ListenerMove extends ModuleListener<PacketFly, MoveEvent>
{
    public ListenerMove(PacketFly module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (module.mode.getValue() != Mode.Compatibility
            && (module.mode.getValue() == Mode.Setback
                    || module.teleportID.get() != 0))
        {
            if (module.zeroSpeed.getValue())
            {
                event.setX(0.0);
                event.setY(0.0);
                event.setZ(0.0);
            }
            else
            {
                event.setX(mc.player.motionX);
                event.setY(mc.player.motionY);
                event.setZ(mc.player.motionZ);
            }

            if (module.zeroY.getValue())
            {
                event.setY(0.0);
            }

            if (module.phase.getValue() == Phase.Semi
                    || module.isPlayerCollisionBoundingBoxEmpty())
            {
                mc.player.noClip = true;
            }
        }
    }

}
