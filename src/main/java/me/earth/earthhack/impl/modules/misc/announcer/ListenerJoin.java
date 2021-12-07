package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.ConnectionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;

final class ListenerJoin extends ModuleListener<Announcer, ConnectionEvent.Join>
{
    public ListenerJoin(Announcer module)
    {
        super(module, ConnectionEvent.Join.class);
    }

    @Override
    public void invoke(ConnectionEvent.Join event)
    {
        if (module.join.getValue()
                && !event.getName()
                         .equals(mc.getSession().getProfile().getName()))
        {
            module.addWordAndIncrement(AnnouncementType.Join, event.getName());
        }
    }

}
