package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerLogout extends ModuleListener<Speedmine, DisconnectEvent>
{
    public ListenerLogout(Speedmine module)
    {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event)
    {
        mc.addScheduledTask(module::reset);
    }

}
