package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

/**
 * Packet which contains serializes a String.
 */
public class AbstractC2SStringPacket extends C2SPacket {
    protected String string;

    public AbstractC2SStringPacket(int id) {
        super(id);
    }

    public AbstractC2SStringPacket(int id, String string) {
        super(id);
        this.string = string;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buffer) {
        this.string = buffer.readString(Short.MAX_VALUE);
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buffer) {
        buffer.writeString(this.string);
    }

    @Override
    public void execute(NetworkManager networkManager) {

    }

    public String getString() {
        return string;
    }

}
