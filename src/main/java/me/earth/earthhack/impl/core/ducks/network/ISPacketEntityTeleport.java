package me.earth.earthhack.impl.core.ducks.network;

public interface ISPacketEntityTeleport {
    boolean hasBeenSetByPackets();

    void setSetByPackets(boolean setByPackets);

}
