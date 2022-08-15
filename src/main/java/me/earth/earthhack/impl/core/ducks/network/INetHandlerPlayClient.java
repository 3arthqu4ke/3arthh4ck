package me.earth.earthhack.impl.core.ducks.network;

import com.mojang.authlib.GameProfile;

/**
 * Duck interface for {@link net.minecraft.client.network.NetHandlerPlayClient}
 * not to confuse with {@link net.minecraft.network.play.INetHandlerPlayClient}.
 */
public interface INetHandlerPlayClient
{
    boolean isDoneLoadingTerrain();

    void setDoneLoadingTerrain(boolean loaded);

    void setGameProfile(GameProfile gameProfile);

}
