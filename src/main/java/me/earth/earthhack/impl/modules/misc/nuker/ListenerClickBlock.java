package me.earth.earthhack.impl.modules.misc.nuker;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerClickBlock extends ModuleListener<Nuker, ClickBlockEvent>
{
    public ListenerClickBlock(Nuker module)
    {
        super(module, ClickBlockEvent.class, 11);
    }

    @Override
    public void invoke(ClickBlockEvent event)
    {
        if (module.nuke.getValue()
                && module.timer.passed(module.delay.getValue())
                && !module.breaking)
        {
            module.currentSelection = module.getSelection(event.getPos());
            module.breaking = true;
            module.breakSelection(module.currentSelection,
                                  module.autoTool.getValue());
            event.setCancelled(true);
        }
    }

}
