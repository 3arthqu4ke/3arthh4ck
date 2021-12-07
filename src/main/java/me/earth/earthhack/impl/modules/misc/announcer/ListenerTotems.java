package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

final class ListenerTotems extends
        ModuleListener<Announcer, PacketEvent.Receive<SPacketEntityStatus>>
{
    public ListenerTotems(Announcer module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityStatus.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityStatus> event)
    {
        if (module.totems.getValue())
        {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35)
            {
                Entity entity = packet.getEntity(mc.world);
                if (entity instanceof EntityPlayer
                        && (!module.friends.getValue() || !Managers.FRIENDS.contains((EntityPlayer) entity)))
                {
                    Announcement announcement = module.addWordAndIncrement
                            (AnnouncementType.Totems, entity.getName());

                    announcement.setAmount(Managers.COMBAT.getPops(entity) + 1);
                }
            }
        }
    }

}
