package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerBlockPush extends ModuleListener<Velocity, BlockPushEvent>
{
    public ListenerBlockPush(Velocity module)
    {
        super(module, BlockPushEvent.class, 1000);
    }

    @Override
    public void invoke(BlockPushEvent event)
    {
        if (module.blocks.getValue())
        {
            event.setCancelled(true);
        }
    }
}
