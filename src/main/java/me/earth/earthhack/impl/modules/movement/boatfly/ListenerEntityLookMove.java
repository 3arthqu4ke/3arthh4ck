package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.world.World;

final class ListenerEntityLookMove extends
        ModuleListener<BoatFly, PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove>>
{
    public ListenerEntityLookMove(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketEntity.S17PacketEntityLookMove.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event)
    {
        World world = mc.world;
        EntityPlayer player = mc.player;
        Entity ridingEntity;
        if (module.noForceBoatMove.getValue()
                && player != null
                && world != null
                && (ridingEntity = player.getRidingEntity()) != null)
        {
            if (ridingEntity.equals(event.getPacket().getEntity(world)))
            {
                event.setCancelled(true);
            }
        }
    }

}
