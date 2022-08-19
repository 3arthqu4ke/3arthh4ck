package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.pingbypass.netty.PbNetworkSystem;

public class PbTickListener extends EventListener<TickEvent> {
    private final PbNetworkSystem system;

    public PbTickListener(PbNetworkSystem system) {
        super(TickEvent.class);
        this.system = system;
    }

    @Override
    public void invoke(TickEvent event) {
        system.networkTick();
    }

}
