package me.earth.earthhack.pingbypass.netty;

import me.earth.earthhack.impl.core.ducks.network.INetworkManager;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.pingbypass.event.PbDisconnectEvent;
import me.earth.earthhack.pingbypass.event.PbPacketEvent;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ITextComponent;

public class PbNetworkManager extends NetworkManager implements INetworkManager {
    public PbNetworkManager(EnumPacketDirection packetDirection) {
        super(packetDirection);
    }

    @Override
    public boolean isPingBypass() {
        return true;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void closeChannel(ITextComponent message) {
        super.closeChannel(message);
    }

    @Override
    public <T extends Packet<?>> PacketEvent.Send<T> getSendEvent(T packet) {
        return new PbPacketEvent.S2C<>(packet);
    }

    @Override
    public <T extends Packet<?>> PacketEvent.Receive<T> getReceive(T packet) {
        return new PbPacketEvent.C2S<>(packet, this);
    }

    @Override
    public <T extends Packet<?>> PacketEvent.Post<T> getPost(T packet) {
        return new PbPacketEvent.C2SPost<>(packet);
    }

    @Override
    public <T extends Packet<?>> PacketEvent.NoEvent<T> getNoEvent(T packet, boolean post) {
        return new PbPacketEvent.S2CNoEvent<>(packet, post);
    }

    @Override
    public DisconnectEvent getDisconnect(ITextComponent component) {
        return new PbDisconnectEvent(component, this);
    }

}
