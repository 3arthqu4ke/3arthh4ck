package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;

final class ListenerLogout extends ModuleListener<Management, DisconnectEvent>
{
    public ListenerLogout(Management module)
    {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event)
    {
        if (module.logout.getValue())
        {
            Managers.COMBAT.reset();
        }
    }

}
