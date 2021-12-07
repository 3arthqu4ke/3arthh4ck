package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerLogout extends ModuleListener<Chat, DisconnectEvent>
{
    public ListenerLogout(Chat module)
    {
        super(module, DisconnectEvent.class);
    }

    @Override
    public void invoke(DisconnectEvent event)
    {
        module.clearNoScroll();
    }

}

