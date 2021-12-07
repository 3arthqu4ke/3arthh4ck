package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;

final class ListenerDeath extends ModuleListener<Announcer, DeathEvent>
{
    public ListenerDeath(Announcer module)
    {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event)
    {
        if (module.autoEZ.getValue())
        {
            //noinspection SuspiciousMethodCalls
            if (module.targets.remove(event.getEntity())
                    && mc.player.getDistanceSq(event.getEntity()) <= 144)
            {
                module.announcements.put(AnnouncementType.Death,
                        new Announcement(event.getEntity().getName(), 0));
                module.announcements.put(AnnouncementType.Totems, null);
            }
        }
    }

}
