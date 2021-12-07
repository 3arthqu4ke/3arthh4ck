package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerPostKeys extends
        ModuleListener<NoSlowDown, KeyboardEvent.Post>
{
    public ListenerPostKeys(NoSlowDown module)
    {
        super(module, KeyboardEvent.Post.class);
    }

    @Override
    public void invoke(KeyboardEvent.Post event)
    {
        module.updateKeyBinds();
    }

}
