package me.earth.earthhack.impl.modules.player.autotool;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerDeath extends ModuleListener<AutoTool, DeathEvent>
{
    public ListenerDeath(AutoTool module)
    {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event)
    {
        if (event.getEntity().equals(mc.player))
        {
            module.reset();
        }
    }

}
