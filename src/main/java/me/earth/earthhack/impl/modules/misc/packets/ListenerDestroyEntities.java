package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketDestroyEntities;

import java.util.List;

final class ListenerDestroyEntities extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketDestroyEntities>>
{
    public ListenerDestroyEntities(Packets module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event)
    {
        if (module.fastDestroyEntities.getValue())
        {
            List<Entity> entities = Managers.ENTITIES.getEntities();
            if (entities == null)
            {
                return;
            }

            for (int id : event.getPacket().getEntityIDs())
            {
                for (Entity entity : entities)
                {
                    if (entity != null && entity.getEntityId() == id)
                    {
                        entity.setDead();
                    }
                }
            }
        }
    }

}

