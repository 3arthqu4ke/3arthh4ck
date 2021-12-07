package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.impl.event.events.render.SuffocationEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerSuffocation extends
        ModuleListener<NoRender, SuffocationEvent>
{
    public ListenerSuffocation(NoRender module)
    {
        super(module, SuffocationEvent.class);
    }

    @Override
    public void invoke(SuffocationEvent event)
    {
        if (module.blocks.getValue())
        {
            event.setCancelled(true);
        }
    }

}
