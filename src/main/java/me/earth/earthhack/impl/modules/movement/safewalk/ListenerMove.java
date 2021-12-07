package me.earth.earthhack.impl.modules.movement.safewalk;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerMove extends ModuleListener<SafeWalk, MoveEvent>
{
    public ListenerMove(SafeWalk module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        event.setSneaking(true);
    }

}
