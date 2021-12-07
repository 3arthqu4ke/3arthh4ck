package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerBreak extends ModuleListener<Step, BlockDestroyEvent>
{
    public ListenerBreak(Step module)
    {
        super(module, BlockDestroyEvent.class);
    }

    @Override
    public void invoke(BlockDestroyEvent event)
    {
        module.onBreak();
    }

}
