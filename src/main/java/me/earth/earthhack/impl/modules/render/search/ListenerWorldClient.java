package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient extends
        ModuleListener<Search, WorldClientEvent.Load>
{
    public ListenerWorldClient(Search module)
    {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event)
    {
        module.toRender.clear();
    }

}
