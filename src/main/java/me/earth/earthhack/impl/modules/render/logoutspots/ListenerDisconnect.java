package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerDisconnect extends ModuleListener<LogoutSpots, DisconnectEvent>
{
    public ListenerDisconnect(LogoutSpots module)
    {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event)
    {
        module.spots.clear();
    }

}
