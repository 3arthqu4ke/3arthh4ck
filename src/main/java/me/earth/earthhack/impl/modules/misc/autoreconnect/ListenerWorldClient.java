package me.earth.earthhack.impl.modules.misc.autoreconnect;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient
        extends ModuleListener<AutoReconnect, WorldClientEvent.Unload>
{
    public ListenerWorldClient(AutoReconnect module)
    {
        super(module, WorldClientEvent.Unload.class);
    }

    @Override
    public void invoke(WorldClientEvent.Unload event)
    {
        module.setServerData();
    }

}
