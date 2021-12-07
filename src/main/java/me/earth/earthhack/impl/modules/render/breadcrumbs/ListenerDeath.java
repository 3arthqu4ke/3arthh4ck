package me.earth.earthhack.impl.modules.render.breadcrumbs;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerDeath extends ModuleListener<BreadCrumbs, DeathEvent>
{
    public ListenerDeath(BreadCrumbs module)
    {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event)
    {
        if (module.clearD.getValue()
                && event.getEntity() != null
                && event.getEntity().equals(mc.player))
        {
            mc.addScheduledTask(module.positions::clear);
        }
    }

}
