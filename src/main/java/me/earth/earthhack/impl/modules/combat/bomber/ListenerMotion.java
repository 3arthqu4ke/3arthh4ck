package me.earth.earthhack.impl.modules.combat.bomber;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerMotion extends ModuleListener<CrystalBomber, MotionUpdateEvent> {

    public ListenerMotion(CrystalBomber module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        module.doCrystalBomber(event);
    }

}
