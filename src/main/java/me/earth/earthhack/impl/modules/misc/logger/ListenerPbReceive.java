package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.event.PbPacketEvent;

final class ListenerPbReceive extends ModuleListener<Logger, PbPacketEvent.C2S<?>> {
    public ListenerPbReceive(Logger module) {
        super(module, PbPacketEvent.C2S.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(PbPacketEvent.C2S<?> event) {
        if (module.c2Pb.getValue()) {
            module.logPacket(event.getPacket(),
                             "PingBypass received Client Packet ",
                             event.isCancelled(),
                             false);
        }
    }

}
