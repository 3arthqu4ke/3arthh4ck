package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerBlockPush extends ModuleListener<Speed, BlockPushEvent>
{
    public ListenerBlockPush(Speed module)
    {
        super(module, BlockPushEvent.class, 999);
    }

    @Override
    public void invoke(BlockPushEvent event)
    {
        if (module.lagOut.getValue())
        {
            event.setCancelled(false);
        }
    }

}
