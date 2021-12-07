package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

final class ListenerTotems extends
        ModuleListener<Notifications, PacketEvent.Receive<SPacketEntityStatus>>
{
    public ListenerTotems(Notifications module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityStatus.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityStatus> event)
    {
        switch (event.getPacket().getOpCode())
        {
            case 3:
                mc.addScheduledTask(() ->
                {
                    if (mc.world != null)
                    {
                        Entity entity = event.getPacket().getEntity(mc.world);
                        if (entity instanceof EntityPlayer)
                        {
                            int pops = Managers.COMBAT.getPops(entity);
                            if (pops > 0)
                            {
                                module.onDeath(entity,
                                        Managers.COMBAT.getPops(entity));
                            }
                        }
                    }
                });
                break;
            case 35:
                mc.addScheduledTask(() ->
                {
                    Entity entity = event.getPacket().getEntity(mc.world);
                    if (entity instanceof EntityPlayer)
                    {
                        module.onPop(entity,
                                     Managers.COMBAT.getPops(entity) + 1);
                    }
                });
                break;
            default:
        }
    }

}
