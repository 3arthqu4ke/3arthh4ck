package me.earth.earthhack.forge.util;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

public class ReplaceNetworkDispatcher extends NetworkDispatcher
{
    public ReplaceNetworkDispatcher(NetworkManager manager)
    {
        super(manager);
    }

    @Override
    public int getOverrideDimension(SPacketJoinGame packetIn)
    {
        return packetIn.getDimension();
    }

}
