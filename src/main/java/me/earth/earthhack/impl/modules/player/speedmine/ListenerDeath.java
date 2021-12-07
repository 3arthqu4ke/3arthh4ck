package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerDeath extends ModuleListener<Speedmine, DeathEvent>
{
    public ListenerDeath(Speedmine module)
    {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event)
    {
        module.reset();
    }

}
