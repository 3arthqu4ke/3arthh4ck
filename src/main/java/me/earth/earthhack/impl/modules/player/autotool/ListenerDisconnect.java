package me.earth.earthhack.impl.modules.player.autotool;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerDisconnect extends ModuleListener<AutoTool, DisconnectEvent>
{
    public ListenerDisconnect(AutoTool module)
    {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event)
    {
        mc.addScheduledTask(module::reset);
    }

}
