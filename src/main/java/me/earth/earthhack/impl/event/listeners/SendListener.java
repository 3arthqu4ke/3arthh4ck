package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.event.bus.api.Invoker;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.Packet;

public class SendListener<P extends Packet<?>>
        extends LambdaListener<PacketEvent.Send<P>>
{
    public SendListener(Class<P> target,
                       Invoker<PacketEvent.Send<P>> invoker)
    {
        this(target, EventBus.DEFAULT_PRIORITY, invoker);
    }

    public SendListener(Class<P> target,
                       int priority,
                       Invoker<PacketEvent.Send<P>> invoker)
    {
        super(PacketEvent.Send.class, priority, target, invoker);
    }

}
