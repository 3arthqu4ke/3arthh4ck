package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerDisconnect
        extends ModuleListener<Announcer, DisconnectEvent>
{
    public ListenerDisconnect(Announcer module)
    {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event)
    {
        mc.addScheduledTask(module::reset);
    }

}
