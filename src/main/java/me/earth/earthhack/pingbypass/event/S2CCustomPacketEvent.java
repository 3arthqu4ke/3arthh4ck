package me.earth.earthhack.pingbypass.event;

import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.pingbypass.protocol.PbPacket;

/**
 * Fired on the PingBypass client when a PbPacket is received.
 */
public class S2CCustomPacketEvent<T extends PbPacket<?>> extends Event {
    private final T packet;

    public S2CCustomPacketEvent(T packet) {
        this.packet = packet;
    }

    public T getPacket() {
        return packet;
    }

}
