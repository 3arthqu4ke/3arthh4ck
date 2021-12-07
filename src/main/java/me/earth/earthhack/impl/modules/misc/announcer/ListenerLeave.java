package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.ConnectionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;

final class ListenerLeave
        extends ModuleListener<Announcer, ConnectionEvent.Leave>
{
    public ListenerLeave(Announcer module)
    {
        super(module, ConnectionEvent.Leave.class);
    }

    @Override
    public void invoke(ConnectionEvent.Leave event)
    {
        if (module.leave.getValue())
        {
            module.addWordAndIncrement(AnnouncementType.Leave,
                                       event.getName());
        }
    }
    
}
