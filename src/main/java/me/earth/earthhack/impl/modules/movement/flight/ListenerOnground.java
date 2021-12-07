package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.impl.event.events.movement.OnGroundEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;

final class ListenerOnground extends ModuleListener<Flight, OnGroundEvent>
{
    public ListenerOnground(Flight module)
    {
        super(module, OnGroundEvent.class);
    }

    @Override
    public void invoke(OnGroundEvent event)
    {
        if (module.animation.getValue()
                && module.mode.getValue() == FlightMode.Normal)
        {
            event.setCancelled(true);
        }
    }

}
