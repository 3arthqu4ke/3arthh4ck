package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerMove extends ModuleListener<Freecam, MoveEvent>
{
    public ListenerMove(Freecam module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        mc.player.noClip = true;
    }

}
