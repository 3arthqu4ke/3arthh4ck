package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient extends
        ModuleListener<Jesus, WorldClientEvent.Load>
{
    public ListenerWorldClient(Jesus module)
    {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event)
    {
        module.timer.reset();
    }

}
