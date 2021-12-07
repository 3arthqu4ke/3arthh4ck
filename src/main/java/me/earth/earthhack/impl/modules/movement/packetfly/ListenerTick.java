package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.event.listeners.ModuleListener;

import java.util.concurrent.TimeUnit;

final class ListenerTick extends ModuleListener<PacketFly, ListenerTick>
{
    public ListenerTick(PacketFly module)
    {
        super(module, ListenerTick.class);
    }

    @Override
    public void invoke(ListenerTick event)
    {
        module.posLooks.entrySet().removeIf(entry ->
                System.currentTimeMillis() - entry.getValue().getTime()
                        > TimeUnit.SECONDS.toMillis(30L));
    }

}
