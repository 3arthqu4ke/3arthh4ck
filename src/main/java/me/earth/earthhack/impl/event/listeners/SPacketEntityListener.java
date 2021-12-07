package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketEntity;

public abstract class SPacketEntityListener extends SubscriberImpl
        implements Globals
{
    public SPacketEntityListener()
    {
        this(EventBus.DEFAULT_PRIORITY);
    }

    public SPacketEntityListener(int priority)
    {
        this.listeners.add(
            new EventListener<PacketEvent.Receive<SPacketEntity>>
                (PacketEvent.Receive.class, priority, SPacketEntity.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<SPacketEntity> event)
            {
                onPacket(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove>>
                (PacketEvent.Receive.class, priority, SPacketEntity.S15PacketEntityRelMove.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event)
            {
                onPosition(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Receive<SPacketEntity.S16PacketEntityLook>>
                (PacketEvent.Receive.class, priority, SPacketEntity.S16PacketEntityLook.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> event)
            {
                onRotation(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove>>
                (PacketEvent.Receive.class,
                        priority,
                        SPacketEntity.S17PacketEntityLookMove.class)
        {
            @Override
            public void invoke
                    (PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event)
            {
                onPositionRotation(event);
            }
        });
    }

    protected abstract void onPacket
            (PacketEvent.Receive<SPacketEntity> event);

    protected abstract void onPosition
            (PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event);

    protected abstract void onRotation
            (PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> event);

    protected abstract void onPositionRotation
            (PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event);

}
