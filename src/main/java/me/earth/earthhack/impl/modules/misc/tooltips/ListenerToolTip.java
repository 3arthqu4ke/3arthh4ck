package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerToolTip extends ModuleListener<ToolTips, ToolTipEvent>
{
    public ListenerToolTip(ToolTips module)
    {
        super(module, ToolTipEvent.class);
    }

    @Override
    public void invoke(ToolTipEvent event)
    {
        if (module.shulkers.getValue()
                && !event.isCancelled()
                && module.drawShulkerToolTip(event.getStack(),
                                             event.getX(),
                                             event.getY(),
                                             null))
        {
            event.setCancelled(true);
        }
    }

}
