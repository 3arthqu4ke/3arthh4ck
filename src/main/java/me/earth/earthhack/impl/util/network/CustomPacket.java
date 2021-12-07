package me.earth.earthhack.impl.util.network;

import net.minecraft.network.EnumConnectionState;

public interface CustomPacket
{
    int getId() throws Exception;

    default EnumConnectionState getState()
    {
        return EnumConnectionState.PLAY;
    }

}
