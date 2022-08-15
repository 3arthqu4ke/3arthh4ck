package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.pingbypass.protocol.ProtocolIds;

public class C2SPasswordPacket extends AbstractC2SStringPacket {
    public C2SPasswordPacket() {
        super(ProtocolIds.C2S_PASSWORD);
    }

    public C2SPasswordPacket(String password) {
        super(ProtocolIds.C2S_PASSWORD, password);
    }

}
