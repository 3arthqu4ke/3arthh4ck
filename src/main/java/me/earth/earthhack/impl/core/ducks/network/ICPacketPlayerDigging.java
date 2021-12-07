package me.earth.earthhack.impl.core.ducks.network;

public interface ICPacketPlayerDigging
{
    void setClientSideBreaking(boolean breaking);

    boolean isClientSideBreaking();

}
