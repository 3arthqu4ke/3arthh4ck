package me.earth.earthhack.impl.modules.movement.antimove;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.antimove.modes.StaticMode;

final class ListenerMove extends ModuleListener<NoMove, MoveEvent>
{
    public ListenerMove(NoMove module)
    {
        super(module, MoveEvent.class, -1000);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (module.mode.getValue() == StaticMode.Stop)
        {
            mc.player.setVelocity(0, 0, 0);
            event.setX(0);
            event.setY(0);
            event.setZ(0);
        }
    }

}
