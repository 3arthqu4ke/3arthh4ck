package me.earth.earthhack.pingbypass.listeners;

import me.earth.earthhack.pingbypass.protocol.c2s.C2SActualPos;

public class PacketFlyService {
    private C2SActualPos actualPos;
    private boolean packetFlying;

    public C2SActualPos getActualPos() {
        return actualPos;
    }

    public void setActualPos(C2SActualPos actualPos) {
        this.actualPos = actualPos;
    }

    public boolean isPacketFlying() {
        return packetFlying;
    }

    public void setPacketFlying(boolean packetFlying) {
        this.packetFlying = packetFlying;
    }

}
