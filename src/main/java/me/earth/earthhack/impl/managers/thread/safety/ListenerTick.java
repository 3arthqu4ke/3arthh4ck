package me.earth.earthhack.impl.managers.thread.safety;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.safety.util.Update;

final class ListenerTick extends ModuleListener<SafetyManager, TickEvent>
{
    public ListenerTick(SafetyManager manager)
    {
        super(manager, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (module.mode.getValue() == Update.Tick)
        {
            module.runThread();
        }
    }

}
