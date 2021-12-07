package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.misc.ResetBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;

final class ListenerReset extends ModuleListener<Speedmine, ResetBlockEvent>
{
    public ListenerReset(Speedmine module)
    {
        super(module, ResetBlockEvent.class);
    }

    @Override
    public void invoke(ResetBlockEvent event)
    {
        if (module.noReset.getValue()
                || module.mode.getValue() == MineMode.Reset)
        {
            event.setCancelled(true);
        }
    }

}
