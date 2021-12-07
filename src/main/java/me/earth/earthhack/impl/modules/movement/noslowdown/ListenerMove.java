package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;

final class ListenerMove extends ModuleListener<NoSlowDown, MoveEvent>
{
    public ListenerMove(NoSlowDown module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (Managers.NCP.passed(250) && ((IEntity) mc.player).inWeb())
        {
            if (mc.player.onGround)
            {
                event.setX(event.getX() * module.websXZ.getValue());
                event.setZ(event.getZ() * module.websXZ.getValue());
            }
            else if (mc.player.movementInput.sneak || !module.sneak.getValue())
            {
                event.setY(event.getY() * module.websY.getValue());
            }
        }
    }

}
