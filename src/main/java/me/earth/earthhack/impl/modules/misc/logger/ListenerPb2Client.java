package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.event.PbPacketEvent;

public class ListenerPb2Client
    extends ModuleListener<Logger, PbPacketEvent.S2C<?>> {
    public ListenerPb2Client(Logger module) {
        super(module, PbPacketEvent.S2C.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(PbPacketEvent.S2C<?> event) {
        if (module.pb2C.getValue()) {
            module.logPacket(event.getPacket(),
                             "PingBypass Server sending to client ",
                             event.isCancelled(),
                             true,
                             // dont allow chat or the printing is
                             // gonna cause a stackoverflow :)
                             // totally didnt find this out the hard way
                             false);
        }
    }

}
