package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.event.bus.api.Invoker;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.Packet;

public class PostSendListener<P extends Packet<?>>
        extends LambdaListener<PacketEvent.Post<P>>
{
    public PostSendListener(Class<P> target,
                            Invoker<PacketEvent.Post<P>> invoker)
    {
        this(target, EventBus.DEFAULT_PRIORITY, invoker);
    }

    public PostSendListener(Class<P> target,
                            int priority,
                            Invoker<PacketEvent.Post<P>> invoker)
    {
        super(PacketEvent.Post.class, priority, target, invoker);
    }

}
