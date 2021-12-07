package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient extends
        ModuleListener<ToolTips, WorldClientEvent.Load>
{
    public ListenerWorldClient(ToolTips module)
    {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event)
    {
        module.spiedPlayers.clear();
    }

}
