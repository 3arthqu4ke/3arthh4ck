package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.event.S2CCustomPacketEvent;

final class ListenerCustomPbPacket extends ModuleListener<Logger, S2CCustomPacketEvent<?>> {
    public ListenerCustomPbPacket(Logger module) {
        super(module, S2CCustomPacketEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(S2CCustomPacketEvent<?> event) {
        if (module.pbCustom.getValue()) {
            module.logPacket(event.getPacket(),
                             "Receiving PingBypass Packet ",
                             event.isCancelled(),
                             false);
        }
    }

}
