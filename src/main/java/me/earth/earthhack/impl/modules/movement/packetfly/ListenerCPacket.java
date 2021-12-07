package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPacket extends CPacketPlayerListener
{
    private final PacketFly packetFly;

    public ListenerCPacket(PacketFly packetFly)
    {
        this.packetFly = packetFly;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event)
    {
        packetFly.onPacketSend(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event)
    {
        packetFly.onPacketSend(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event)
    {
        packetFly.onPacketSend(event);
    }

    @Override
    protected void onPositionRotation(
            PacketEvent.Send<CPacketPlayer.PositionRotation> event)
    {
        packetFly.onPacketSend(event);
    }

}
