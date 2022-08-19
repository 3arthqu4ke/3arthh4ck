package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

final class ListenerPacket extends ModuleListener<Freecam, PacketEvent.Send<?>>
{
    public ListenerPacket(Freecam module)
    {
        super(module, PacketEvent.Send.class);
    }

    @Override
    public void invoke(PacketEvent.Send<?> event)
    {
        switch (module.mode.getValue())
        {
            case Cancel:
                if (event.getPacket() instanceof CPacketPlayer)
                {
                    event.setCancelled(true);
                }
                break;
            case Spanish:
                Packet<?> packet = event.getPacket();
                if (!(packet instanceof CPacketUseEntity)
                        && !(packet instanceof CPacketPlayerTryUseItem)
                        && !(packet instanceof CPacketPlayerTryUseItemOnBlock)
                        && !(packet instanceof CPacketPlayer)
                        && !(packet instanceof CPacketVehicleMove)
                        && !(packet instanceof CPacketChatMessage)
                        && !(packet instanceof CPacketKeepAlive))
                {
                    event.setCancelled(true);
                }
                break;
            case Position:
                break;
        }
    }

}
