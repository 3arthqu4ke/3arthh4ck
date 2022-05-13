package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.logger.util.LoggerMode;

final class ListenerSend extends ModuleListener<Logger, PacketEvent.Send<?>>
{
    public ListenerSend(Logger module)
    {
        super(module, PacketEvent.Send.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(PacketEvent.Send<?> event)
    {
        if (module.outgoing.getValue()
                && module.mode.getValue() == LoggerMode.Normal)
        {
            module.logPacket(event.getPacket(),
                             "Sending ",
                             event.isCancelled(),
                             true);
        }
    }

}
