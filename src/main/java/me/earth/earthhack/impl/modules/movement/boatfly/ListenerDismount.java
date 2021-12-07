package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSetPassengers;

final class ListenerDismount extends
        ModuleListener<BoatFly, PacketEvent.Receive<SPacketSetPassengers>>
{
    public ListenerDismount(BoatFly module)
    {
        super(module, PacketEvent.Receive.class, SPacketSetPassengers.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSetPassengers> event)
    {
        EntityPlayer player = mc.player;
        if (player == null)
        {
            return;
        }

        Entity riding = mc.player.getRidingEntity();
        if (riding != null
                && event.getPacket().getEntityId() == riding.getEntityId()
                && module.remount.getValue())
        {
            event.setCancelled(true);
            if (module.schedule.getValue())
            {
                mc.addScheduledTask(() ->
                    remove(event.getPacket(), player, riding));
            }
            else
            {
                remove(event.getPacket(), player, riding);
            }
        }
    }

    private void remove(SPacketSetPassengers packet,
                        Entity player,
                        Entity riding)
    {
        for (int id : packet.getPassengerIds())
        {
            if (id == player.getEntityId())
            {
                if (module.remountPackets.getValue())
                {
                    module.sendPackets(riding);
                }
            }
            else
            {
                try
                {
                    Entity entity = mc.world.getEntityByID(id);
                    if (entity != null)
                    {
                        entity.dismountRidingEntity();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
