package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntityHeadLook;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityHeadLook;

final class ListenerHeadLook extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketEntityHeadLook>>
{
    public ListenerHeadLook(Packets module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityHeadLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityHeadLook> event)
    {
        if (module.fastHeadLook.getValue() && !event.isCancelled())
        {
            ISPacketEntityHeadLook p = (ISPacketEntityHeadLook) event.getPacket();
            Entity entity = Managers.ENTITIES.getEntity(p.getEntityId());

            if (entity != null)
            {
                entity.setRotationYawHead(
                    (float)(event.getPacket().getYaw() * 360) / 256.0f);
            }
        }
    }

}
