package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerKeyPress extends
        ModuleListener<Speedmine, KeyboardEvent>
{
    public ListenerKeyPress(Speedmine module)
    {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event)
    {
        if (event.getEventState()
            && event.getKey() == module.breakBind.getValue().getKey())
        {
            module.tryBreak();
        }
    }

}
