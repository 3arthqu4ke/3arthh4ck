package me.earth.earthhack.impl.modules.misc.mcf;

import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class PickBlockListener extends ModuleListener<MCF, ClickMiddleEvent>
{
    public PickBlockListener(MCF module)
    {
        super(module, ClickMiddleEvent.class);
    }

    @Override
    public void invoke(ClickMiddleEvent event)
    {
        if (event.isModuleCancelled() || !module.pickBlock.getValue())
        {
            return;
        }

        module.onMiddleClick();
    }

}
