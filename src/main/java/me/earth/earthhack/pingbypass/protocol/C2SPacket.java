package me.earth.earthhack.pingbypass.protocol;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.impl.util.network.CustomPacket;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.io.IOException;

@SuppressWarnings("NullableProblems")
public abstract class C2SPacket extends CPacketCustomPayload
    implements CustomPacket, PbPacket<INetHandlerPlayServer> {
    private static final CPacketCustomPayload CPCP = new CPacketCustomPayload();
    private final int id;

    public C2SPacket(int id) {
        super("PingBypass", new PacketBuffer(Unpooled.buffer()));
        this.id = id;
    }

    @Override
    public int getId() throws Exception {
        return getState().getPacketId(EnumPacketDirection.SERVERBOUND, CPCP);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        synchronized(this.getBufferData()) { // This may be access multiple times, from multiple threads, lets be safe.
            this.getBufferData().writeVarInt(id);
            this.writeInnerBuffer(this.getBufferData());
            super.writePacketData(buf);
        }
    }

}
