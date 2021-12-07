package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient extends
        ModuleListener<Announcer, WorldClientEvent>
{
    public ListenerWorldClient(Announcer module)
    {
        super(module, WorldClientEvent.class);
    }

    @Override
    public void invoke(WorldClientEvent event)
    {
        module.reset();
    }

}
