package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketVehicleMove;

final class ListenerPostVehicleMove extends
        ModuleListener<BoatFly, PacketEvent.Post<CPacketVehicleMove>>
{
    public ListenerPostVehicleMove(BoatFly module)
    {
        super(module, PacketEvent.Post.class, CPacketVehicleMove.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketVehicleMove> event)
    {
        Entity riding = mc.player.getRidingEntity();
        if (riding != null
                && !module.packetSet.contains(event.getPacket())
                && module.bypass.getValue()
                && module.postBypass.getValue()
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
