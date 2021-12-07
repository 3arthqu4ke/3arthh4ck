package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.impl.event.events.render.RenderEntityEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderEntity
        extends ModuleListener<Chams, RenderEntityEvent>
{
    public ListenerRenderEntity(Chams module)
    {
        super(module, RenderEntityEvent.class);
    }

    @Override
    public void invoke(RenderEntityEvent event)
    {
        if (true) return; // we will need this later for stenciling
        if (module.isValid(event.getEntity()) && !module.force) event.setCancelled(true);
    }
}
