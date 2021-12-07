package me.earth.earthhack.impl.modules.movement.autosprint;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;

final class ListenerTick extends ModuleListener<AutoSprint, TickEvent>
{
    public ListenerTick(AutoSprint module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if ((AutoSprint.canSprint() && (module.mode.getValue() == SprintMode.Legit)) || (AutoSprint.canSprintBetter() && (module.mode.getValue() == SprintMode.Rage)))
        {
            module.mode.getValue().sprint();
        }
    }

}
