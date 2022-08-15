package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;

final class ListenerBlockPush extends ModuleListener<PacketFly, BlockPushEvent>
{
    public ListenerBlockPush(PacketFly module)
    {
        super(module, BlockPushEvent.class);
    }

    @Override
    public void invoke(BlockPushEvent event)
    {
        if (module.mode.getValue() != Mode.Compatibility)
        {
            event.setCancelled(true);
        }
    }

}
