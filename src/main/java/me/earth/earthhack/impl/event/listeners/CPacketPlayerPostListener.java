package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;

public abstract class CPacketPlayerPostListener extends SubscriberImpl
{
    public CPacketPlayerPostListener()
    {
        this(EventBus.DEFAULT_PRIORITY);
    }

    public CPacketPlayerPostListener(int priority)
    {
        this.listeners.add(
            new EventListener<PacketEvent.Post<CPacketPlayer>>
                (PacketEvent.Post.class, priority, CPacketPlayer.class)
        {
            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer> event)
            {
                onPacket(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Post<CPacketPlayer.Position>>
                (PacketEvent.Post.class, priority, CPacketPlayer.Position.class)
        {
            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.Position> event)
            {
                onPosition(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Post<CPacketPlayer.Rotation>>
                (PacketEvent.Post.class, priority, CPacketPlayer.Rotation.class)
        {
            @Override
            public void invoke(PacketEvent.Post<CPacketPlayer.Rotation> event)
            {
                onRotation(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Post<CPacketPlayer.PositionRotation>>
                (PacketEvent.Post.class,
                        priority,
                        CPacketPlayer.PositionRotation.class)
        {
            @Override
            public void invoke
                    (PacketEvent.Post<CPacketPlayer.PositionRotation> event)
            {
                onPositionRotation(event);
            }
        });
    }

    protected abstract void onPacket
            (PacketEvent.Post<CPacketPlayer> event);

    protected abstract void onPosition
            (PacketEvent.Post<CPacketPlayer.Position> event);

    protected abstract void onRotation
            (PacketEvent.Post<CPacketPlayer.Rotation> event);

    protected abstract void onPositionRotation
            (PacketEvent.Post<CPacketPlayer.PositionRotation> event);
}
