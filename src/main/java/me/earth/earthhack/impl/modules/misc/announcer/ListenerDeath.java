package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

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
            EntityPlayerSP player = mc.player;
            //noinspection SuspiciousMethodCalls
            if (player != null
                && !player.equals(event.getEntity())
                && event.getEntity() instanceof EntityPlayer
                && (!module.friends.getValue()
                    || !Managers.FRIENDS.contains(event.getEntity()))
                && (!module.targetsOnly.getValue()
                    || module.targets.remove(event.getEntity()))
                && mc.player.getDistanceSq(event.getEntity()) <= 144)
            {
                module.announcements.put(AnnouncementType.Death,
                        new Announcement(event.getEntity().getName(), 0));
                module.announcements.put(AnnouncementType.Totems, null);
            }
        }
    }

}
