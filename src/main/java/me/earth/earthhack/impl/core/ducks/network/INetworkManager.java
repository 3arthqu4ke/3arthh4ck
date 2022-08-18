package me.earth.earthhack.impl.core.ducks.network;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.IntegratedDisconnectEvent;
import me.earth.earthhack.impl.event.events.network.IntegratedPacketEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.pingbypass.netty.PbNetworkManager;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ITextComponent;

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
    default Packet<?> sendPacketNoEvent(Packet<?> packetIn) {
        return packetIn;
    }

    /**
     * Sends a Packet without creating PacketEvent.Send.
     * A PacketEvent.Post will only be created if post is true.
     *
     * @param packetIn the packet to send.
     * @param post if you want to fire a post event.
     * @return the packet sent, or <tt>null</tt> if the channel is closed.
     */
    default Packet<?> sendPacketNoEvent(Packet<?> packetIn, boolean post) {
        return packetIn;
    }

    default boolean isPingBypass() {
        // this statement is correct but is also overridden in the Mixin and the PbNetworkManager
        return this instanceof PbNetworkManager;
    }

    default EnumPacketDirection getPacketDirection() {
        // dummy, this is overridden in the Mixin
        return EnumPacketDirection.CLIENTBOUND;
    }

    default boolean isIntegratedServerNetworkManager() {
        return !isPingBypass() && getPacketDirection() == EnumPacketDirection.SERVERBOUND;
    }

    default <T extends Packet<?>> PacketEvent.Send<T> getSendEvent(T packet) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedPacketEvent.Send<>(packet);
        }

        return new PacketEvent.Send<>(packet);
    }

    default <T extends Packet<?>> PacketEvent.Receive<T> getReceive(T packet) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedPacketEvent.Receive<>(packet, (NetworkManager) this);
        }

        return new PacketEvent.Receive<>(packet, (NetworkManager) this);
    }

    default <T extends Packet<?>> PacketEvent.Post<T> getPost(T packet) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedPacketEvent.Post<>(packet);
        }

        return new PacketEvent.Post<>(packet);
    }

    default <T extends Packet<?>> PacketEvent.NoEvent<T> getNoEvent(T packet, boolean post) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedPacketEvent.NoEvent<>(packet, post);
        }

        return new PacketEvent.NoEvent<>(packet, post);
    }

    default DisconnectEvent getDisconnect(ITextComponent component) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedDisconnectEvent(component, (NetworkManager) this);
        }

        return new DisconnectEvent(component, (NetworkManager) this);
    }

}
