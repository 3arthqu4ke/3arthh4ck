package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.math.StopWatch;

public class ServerManager extends SubscriberImpl
{
    private final StopWatch timer = new StopWatch();

    public ServerManager()
    {
        this.listeners.add(
            new EventListener<PacketEvent.Receive<?>>(PacketEvent.Receive.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<?> event)
            {
                timer.reset();
            }
        });
    }

    public long lastResponse()
    {
        return timer.getTime();
    }

}
