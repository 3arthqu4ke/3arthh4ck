package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.util.BookCrashMode;

final class ListenerWorldClient
        extends ModuleListener<Packets, WorldClientEvent.Load>
{
    public ListenerWorldClient(Packets module)
    {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event)
    {
        module.bookCrash.setValue(BookCrashMode.None);
        module.offhandCrashes.setValue(0);
    }
}
