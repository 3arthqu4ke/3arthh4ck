package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient
        extends ModuleListener<AutoMine, WorldClientEvent.Load>
{
    public ListenerWorldClient(AutoMine module)
    {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event)
    {
        module.reset(true);
        module.blackList.clear();
    }

}
