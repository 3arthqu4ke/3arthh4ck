package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.client.InitEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;

class ListenerInit extends ModuleListener<PingBypassModule, InitEvent> {
    public ListenerInit(PingBypassModule module) {
        super(module, InitEvent.class);
    }

    @Override
    public void invoke(InitEvent event) {
        module.pbSerializer.addModules(PingBypass.MODULES);
        module.getListeners().addAll(module.pbSerializer.getListeners());
        if (Bus.EVENT_BUS.isSubscribed(module)) {
            module.pbSerializer.getListeners().forEach(Bus.EVENT_BUS::register);
        }

        Bus.EVENT_BUS.unregister(this);
    }

}
