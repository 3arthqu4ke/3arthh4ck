package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.pingbypass.PingBypass;

public class PbMoveListener extends EventListener<MoveEvent> {
    public PbMoveListener() {
        super(MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (PingBypass.isConnected()) {
            event.setCancelled(true);
        }
    }

}
