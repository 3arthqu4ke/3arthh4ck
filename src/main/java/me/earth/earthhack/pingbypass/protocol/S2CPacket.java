package me.earth.earthhack.pingbypass.protocol;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.impl.util.network.CustomPacket;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketCustomPayload;

import java.io.IOException;

@SuppressWarnings("NullableProblems")
public abstract class S2CPacket extends SPacketCustomPayload
    implements CustomPacket, PbPacket<INetHandlerPlayClient> {
    private static final SPacketCustomPayload SPCP = new SPacketCustomPayload();
    private final int id;

    public S2CPacket(int id) {
        super("PingBypass", new PacketBuffer(Unpooled.buffer()));
        this.id = id;
    }

    @Override
    public void execute(NetworkManager networkManager) {
        // NOP
    }

    @Override
    public int getId() throws Exception {
        return getState().getPacketId(EnumPacketDirection.CLIENTBOUND, SPCP);
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
