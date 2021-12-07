package me.earth.earthhack.impl.core.ducks.network;

import net.minecraft.network.Packet;

/**
 * Duck interface for {@link net.minecraft.network.NetworkManager}.
 */
public interface INetworkManager
{
    /**
     * Invokes {@link INetworkManager#sendPacketNoEvent(Packet, boolean)},
     * for the packet and <tt>true</tt>;
     *
     * @param packetIn the packet to send.
     * @return the packet sent, or <tt>null</tt> if the channel is closed.
     */
    Packet<?> sendPacketNoEvent(Packet<?> packetIn);

    /**
     * Sends a Packet without creating PacketEvent.Send.
     * A PacketEvent.Post will only be created if post is true.
     *
     * @param packetIn the packet to send.
     * @param post if you want to fire a post event.
     * @return the packet sent, or <tt>null</tt> if the channel is closed.
     */
    Packet<?> sendPacketNoEvent(Packet<?> packetIn, boolean post);

}
