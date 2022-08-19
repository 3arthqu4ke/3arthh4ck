package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerPickBlock extends
        ModuleListener<MiddleClickPearl, ClickMiddleEvent>
{
    public ListenerPickBlock(MiddleClickPearl module)
    {
        super(module, ClickMiddleEvent.class, 11);
    }

    @Override
    public void invoke(ClickMiddleEvent event)
    {
        if (!event.isModuleCancelled()
            && module.pickBlock.getValue()
            && !event.isCancelled())
        {
            module.onClick(event);
        }
    }

}
