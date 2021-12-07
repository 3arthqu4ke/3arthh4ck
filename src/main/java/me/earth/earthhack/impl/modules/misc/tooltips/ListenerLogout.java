package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerLogout extends ModuleListener<ToolTips, DisconnectEvent>
{
    public ListenerLogout(ToolTips module)
    {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event)
    {
        module.spiedPlayers.clear();
    }

}
