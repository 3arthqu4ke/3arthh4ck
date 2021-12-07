package me.earth.earthhack.impl.modules.movement.entitycontrol;

import me.earth.earthhack.impl.event.events.movement.ControlEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerControl extends ModuleListener<EntityControl, ControlEvent>
{
    public ListenerControl(EntityControl module)
    {
        super(module, ControlEvent.class);
    }

    @Override
    public void invoke(ControlEvent event)
    {
        if (module.control.getValue())
        {
            event.setCancelled(true);
        }
    }

}
