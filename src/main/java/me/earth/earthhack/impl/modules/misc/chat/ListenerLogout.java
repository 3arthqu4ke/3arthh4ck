package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.network.ConnectionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerLogout extends ModuleListener<Chat, ConnectionEvent.Leave>
{
    public ListenerLogout(Chat module) {
        super(module, ConnectionEvent.Leave.class);
    }

    @Override
    public void invoke(ConnectionEvent.Leave event) {
        if (event.getName() != null) {
            module.sent.remove(event.getName());
        } else if (event.getPlayer() != null) {
            module.sent.remove(event.getPlayer().getName());
        }
    }

}
