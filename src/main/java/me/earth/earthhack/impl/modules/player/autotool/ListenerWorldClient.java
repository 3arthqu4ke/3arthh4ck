package me.earth.earthhack.impl.modules.player.autotool;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient extends ModuleListener<AutoTool, WorldClientEvent>
{
    public ListenerWorldClient(AutoTool module)
    {
        super(module, WorldClientEvent.class);
    }

    @Override
    public void invoke(WorldClientEvent event)
    {
        module.reset();
    }

}
