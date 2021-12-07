package me.earth.earthhack.impl.modules.player.replenish;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerDeath extends ModuleListener<Replenish, DeathEvent>
{
    public ListenerDeath(Replenish module)
    {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event)
    {
        if (event.getEntity().equals(mc.player))
        {
            module.clear();
        }
    }

}
