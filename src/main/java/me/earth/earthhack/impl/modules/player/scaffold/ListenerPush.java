package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerPush extends ModuleListener<Scaffold, BlockPushEvent>
{
    public ListenerPush(Scaffold module)
    {
        super(module, BlockPushEvent.class);
    }

    @Override
    public void invoke(BlockPushEvent event)
    {
        if (module.tower.getValue())
        {
            event.setCancelled(true);
        }
    }

}
