package me.earth.earthhack.impl.modules.client.server.protocol;

import me.earth.earthhack.impl.util.network.CustomPacket;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CopyPacket
        implements Packet<INetHandlerPlayServer>, CustomPacket
{
    private final byte[] buffer;
    private final int ordinal;
    private final int offset;
    private final int id;

    public CopyPacket(int id, int ordinal, byte[] buffer)
    {
        this(id, ordinal, buffer, 0);
    }

    public CopyPacket(int id, int ordinal, byte[] buffer, int offset)
    {
        this.id      = id;
        this.ordinal = ordinal;
        this.buffer  = buffer;
        this.offset  = offset;
    }

    @Override
    public int getId() throws Exception
    {
        return id;
    }

    @Override
    public EnumConnectionState getState()
    {
        return EnumConnectionState.values()[ordinal];
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void readPacketData(PacketBuffer buf)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writePacketData(PacketBuffer buf)
    {
        buf.writeBytes(buffer, offset, buffer.length - offset);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void processPacket(INetHandlerPlayServer handler)
    {
        throw new UnsupportedOperationException();
    }

}
