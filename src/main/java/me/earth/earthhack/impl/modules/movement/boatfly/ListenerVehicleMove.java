package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketVehicleMove;

final class ListenerVehicleMove extends
        ModuleListener<BoatFly, PacketEvent.Send<CPacketVehicleMove>>
{
    public ListenerVehicleMove(BoatFly module)
    {
        super(module, PacketEvent.Send.class, CPacketVehicleMove.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketVehicleMove> event)
    {
        Entity riding = mc.player.getRidingEntity();
        if (mc.player.getRidingEntity() != null
                && !module.packetSet.contains(event.getPacket())
                && module.bypass.getValue()
                && !module.postBypass.getValue()
                && module.tickCount++ >= module.ticks.getValue())
        {
            for (int i = 0; i <= module.packets.getValue(); i++)
            {
                module.sendPackets(riding);
            }

            module.tickCount = 0;
        }
    }

}
