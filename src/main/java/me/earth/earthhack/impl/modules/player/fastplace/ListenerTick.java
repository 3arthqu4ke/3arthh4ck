package me.earth.earthhack.impl.modules.player.fastplace;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerTick extends ModuleListener<FastPlace, TickEvent>
{
    public ListenerTick(FastPlace module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (mc.player != null)
        {
            module.onTick();
        }
    }

}
