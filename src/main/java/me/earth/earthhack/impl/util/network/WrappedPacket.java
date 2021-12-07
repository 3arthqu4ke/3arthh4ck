package me.earth.earthhack.impl.util.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

/**
 * Wraps a {@link Packet} into a {@link CustomPacket}.
 * Only works with packets that are {@link EnumPacketDirection#SERVERBOUND}.
 */
public class WrappedPacket
        implements Packet<INetHandlerPlayServer>, CustomPacket
{
    private final Packet<INetHandlerPlayServer> wrapped;

    /**
     * Wraps the given packet in a CustomPacket.
     * It's really important that the given packet doesn't
     * form any loops with this packet. To be safe just
     * don't pass other CustomPackets in this constructor
     * unless you know what you are doing.
     *
     * @param wrapped the packet to wrap.
     */
    public WrappedPacket(Packet<INetHandlerPlayServer> wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public int getId() throws Exception
    {
        return getState().getPacketId(EnumPacketDirection.SERVERBOUND, wrapped);
    }

    @Override
    public EnumConnectionState getState()
    {
        return EnumConnectionState.getFromPacket(wrapped);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void readPacketData(PacketBuffer buf)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        wrapped.writePacketData(buf);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void processPacket(INetHandlerPlayServer handler)
    {
        throw new UnsupportedOperationException();
    }

}
