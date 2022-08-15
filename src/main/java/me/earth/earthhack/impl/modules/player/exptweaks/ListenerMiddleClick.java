package me.earth.earthhack.impl.modules.player.exptweaks;

import me.earth.earthhack.impl.event.events.keyboard.MouseEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerMiddleClick
    extends ModuleListener<ExpTweaks, MouseEvent> {
    public ListenerMiddleClick(ExpTweaks module) {
        super(module, MouseEvent.class);
    }

    @Override
    public void invoke(MouseEvent event) {
        if (module.normalMC.getValue()) {
            ListenerPickBlock.mayCancel(module, event);
        }
    }

}
