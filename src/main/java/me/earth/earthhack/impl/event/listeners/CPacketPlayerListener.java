package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;

/**
 * Subscribing to all CPacketPlayers is a tedious process
 * so this is a listener that makes that process easier.
 * Just add all the listeners to your Subscribers listeners
 * or subscribe this subscriber to the bus.
 */
public abstract class CPacketPlayerListener extends SubscriberImpl
{
    public CPacketPlayerListener()
    {
        this(EventBus.DEFAULT_PRIORITY);
    }

    public CPacketPlayerListener(int priority)
    {
        this.listeners.add(
            new EventListener<PacketEvent.Send<CPacketPlayer>>
                (PacketEvent.Send.class, priority, CPacketPlayer.class)
        {
            @Override
            public void invoke(PacketEvent.Send<CPacketPlayer> event)
            {
                onPacket(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Send<CPacketPlayer.Position>>
                (PacketEvent.Send.class, priority, CPacketPlayer.Position.class)
        {
            @Override
            public void invoke(PacketEvent.Send<CPacketPlayer.Position> event)
            {
                onPosition(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Send<CPacketPlayer.Rotation>>
                (PacketEvent.Send.class, priority, CPacketPlayer.Rotation.class)
        {
            @Override
            public void invoke(PacketEvent.Send<CPacketPlayer.Rotation> event)
            {
                onRotation(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Send<CPacketPlayer.PositionRotation>>
                (PacketEvent.Send.class,
                        priority,
                        CPacketPlayer.PositionRotation.class)
        {
            @Override
            public void invoke
                    (PacketEvent.Send<CPacketPlayer.PositionRotation> event)
            {
                onPositionRotation(event);
            }
        });
    }

    protected abstract void onPacket
            (PacketEvent.Send<CPacketPlayer> event);

    protected abstract void onPosition
            (PacketEvent.Send<CPacketPlayer.Position> event);

    protected abstract void onRotation
            (PacketEvent.Send<CPacketPlayer.Rotation> event);

    protected abstract void onPositionRotation
            (PacketEvent.Send<CPacketPlayer.PositionRotation> event);

}
