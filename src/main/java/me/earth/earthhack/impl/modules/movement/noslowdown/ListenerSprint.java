package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.movement.SprintEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerSprint extends ModuleListener<NoSlowDown, SprintEvent>
{
    public ListenerSprint(NoSlowDown module)
    {
        super(module, SprintEvent.class);
    }

    @Override
    public void invoke(SprintEvent event)
    {
        if (module.sprint.getValue() && module.items.getValue())
        {
            event.setCancelled(true);
        }
    }

}
