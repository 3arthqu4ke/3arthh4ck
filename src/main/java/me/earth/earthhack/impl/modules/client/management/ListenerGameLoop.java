package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerGameLoop extends ModuleListener<Management, GameLoopEvent>
{
    public ListenerGameLoop(Management module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        if (mc.world != null && module.time.getValue() != 0)
        {
            mc.world.setWorldTime(-module.time.getValue());
        }
    }

}
