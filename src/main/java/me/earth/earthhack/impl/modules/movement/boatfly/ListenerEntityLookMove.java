package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketEntity;

final class ListenerEntityLookMove extends
        ModuleListener<BoatFly, PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove>>
{

    public ListenerEntityLookMove(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketEntity.S17PacketEntityLookMove.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event) {
        if (mc.player.getRidingEntity() != null && module.noForceBoatMove.getValue())
        {
            if (event.getPacket().getEntity(mc.world) == mc.player.getRidingEntity())
            {
                event.setCancelled(true);
            }
        }
    }

}
