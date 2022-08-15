package me.earth.earthhack.impl.modules.misc.mcf;

import me.earth.earthhack.impl.event.events.keyboard.MouseEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class MouseListener extends ModuleListener<MCF, MouseEvent> {
    public MouseListener(MCF module) {
        super(module, MouseEvent.class);
    }

    @Override
    public void invoke(MouseEvent event) {
        if (event.getButton() == 2
            && event.getState()
            && !event.isCancelled()
            && !module.pickBlock.getValue()) {
            module.onMiddleClick();
        }
    }
    
}
