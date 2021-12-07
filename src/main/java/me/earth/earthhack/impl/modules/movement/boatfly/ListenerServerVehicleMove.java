package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketMoveVehicle;

final class ListenerServerVehicleMove extends
        ModuleListener<BoatFly, PacketEvent.Receive<SPacketMoveVehicle>>
{
    public ListenerServerVehicleMove(BoatFly module)
    {
        super(module, PacketEvent.Receive.class, SPacketMoveVehicle.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMoveVehicle> event)
    {
        if (module.noVehicleMove.getValue())
        {
            event.setCancelled(true);
        }
    }

}
