package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.impl.event.events.keyboard.MouseEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerMiddleClick extends ModuleListener<MiddleClickPearl, MouseEvent> {
    public ListenerMiddleClick(MiddleClickPearl module) {
        super(module, MouseEvent.class);
    }

    @Override
    public void invoke(MouseEvent event) {
        if (event.getButton() == 2
            && event.getState()
            && !event.isCancelled()
            && !module.pickBlock.getValue()) {
            module.onClick(event);
        }
    }

}
