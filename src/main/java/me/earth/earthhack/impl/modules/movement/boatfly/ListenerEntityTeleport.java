package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketEntityTeleport;

final class ListenerEntityTeleport extends
        ModuleListener<BoatFly, PacketEvent.Receive<SPacketEntityTeleport>>
{

    public ListenerEntityTeleport(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketEntityTeleport.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityTeleport> event) {
        EntityPlayerSP player = mc.player;
        if (player != null && player.getRidingEntity() != null && module.noForceBoatMove.getValue())
        {
            if (event.getPacket().getEntityId() == player.getRidingEntity().getEntityId())
            {
                event.setCancelled(true);
            }
        }
    }

}
