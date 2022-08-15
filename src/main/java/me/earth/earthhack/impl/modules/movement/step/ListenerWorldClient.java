package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient
    extends ModuleListener<Step, WorldClientEvent.Load> {
    public ListenerWorldClient(Step module) {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event) {
        module.reset();
    }

}
