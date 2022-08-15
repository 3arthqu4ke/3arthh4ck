package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

public class C2SJoinPacket extends C2SPacket {
    private String ip;

    public C2SJoinPacket() {
        super(ProtocolIds.C2S_JOIN);
    }

    public C2SJoinPacket(String ip) {
        super(ProtocolIds.C2S_JOIN);
        this.ip = ip;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buffer) {
        this.ip = buffer.readString(Short.MAX_VALUE);
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buffer) {
        buffer.writeString(this.ip);
    }

    @Override
    public void execute(NetworkManager networkManager) {

    }

}
