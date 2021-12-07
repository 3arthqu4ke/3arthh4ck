package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerTick extends
        ModuleListener<ServerAutoCrystal, KeyboardEvent.Post>
{
    public ListenerTick(ServerAutoCrystal module)
    {
        super(module, KeyboardEvent.Post.class);
    }

    @Override
    public void invoke(KeyboardEvent.Post event)
    {
        module.onTick();
    }

}
