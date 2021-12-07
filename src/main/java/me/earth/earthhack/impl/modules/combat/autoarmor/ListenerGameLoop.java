package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerGameLoop extends ModuleListener<AutoArmor, GameLoopEvent>
{
    public ListenerGameLoop(AutoArmor module)
    {
        super(module, GameLoopEvent.class, -5);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        module.runClick();
    }

}
