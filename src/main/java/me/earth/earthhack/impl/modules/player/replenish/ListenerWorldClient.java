package me.earth.earthhack.impl.modules.player.replenish;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient extends
        ModuleListener<Replenish, WorldClientEvent>
{
    public ListenerWorldClient(Replenish module)
    {
        super(module, WorldClientEvent.class);
    }

    @Override
    public void invoke(WorldClientEvent event)
    {
        module.clear();
    }

}
