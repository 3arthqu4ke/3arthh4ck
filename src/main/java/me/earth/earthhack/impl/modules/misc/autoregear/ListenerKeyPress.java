package me.earth.earthhack.impl.modules.misc.autoregear;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerKeyPress
        extends ModuleListener<AutoRegear, KeyboardEvent>
{
    public ListenerKeyPress(AutoRegear module)
    {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event)
    {
        if (event.getKey() == module.regear.getValue().getKey())
        {
            module.shouldRegear = true;
        }
    }
}
