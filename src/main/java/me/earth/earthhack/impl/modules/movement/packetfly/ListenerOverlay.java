package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.event.events.render.SuffocationEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerOverlay extends ModuleListener<PacketFly, SuffocationEvent>
{
    public ListenerOverlay(PacketFly module)
    {
        super(module, SuffocationEvent.class);
    }

    @Override
    public void invoke(SuffocationEvent event)
    {
        event.setCancelled(true);
    }

}
