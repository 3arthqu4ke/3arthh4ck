package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerGameLoop extends ModuleListener<Offhand, GameLoopEvent>
{
    public ListenerGameLoop(Offhand module)
    {
        super(module, GameLoopEvent.class, Integer.MAX_VALUE);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        module.doOffhand();
    }

}
