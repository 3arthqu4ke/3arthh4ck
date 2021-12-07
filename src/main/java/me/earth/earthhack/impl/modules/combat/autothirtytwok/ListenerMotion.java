package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerMotion extends ModuleListener<Auto32k, MotionUpdateEvent> {

    public ListenerMotion(Auto32k module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        module.onUpdateWalkingPlayer(event);
    }

}
