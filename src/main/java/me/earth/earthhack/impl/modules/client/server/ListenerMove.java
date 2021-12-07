package me.earth.earthhack.impl.modules.client.server;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;

final class ListenerMove extends ModuleListener<ServerModule, MoveEvent>
{
    public ListenerMove(ServerModule module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (module.currentMode == ServerMode.Client && module.sync.getValue())
        {
            event.setX(module.getLastX());
            event.setY(module.getLastY());
            event.setZ(module.getLastZ());
        }
    }

}
