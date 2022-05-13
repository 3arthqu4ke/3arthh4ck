package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerReceive extends
        ModuleListener<Logger, PacketEvent.Receive<?>>
{
    public ListenerReceive(Logger module)
    {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(PacketEvent.Receive<?> event)
    {
        if (module.incoming.getValue())
        {
            module.logPacket(event.getPacket(),
                             "Receiving ",
                             event.isCancelled(),
                             false);
        }
    }

}
