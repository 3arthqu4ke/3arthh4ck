package me.earth.earthhack.impl.core.ducks.network;

public interface ICPacketPlayerDigging
{
    void setClientSideBreaking(boolean breaking);

    boolean isClientSideBreaking();

    void setNormalDigging(boolean normalDigging);

    /**
     * Signalizes that this packet is coming from Minecrafts code rather
     * than from the client and should not get send though the PingBypass
     * when Speedmine is active.
     */
    boolean isNormalDigging();

}
