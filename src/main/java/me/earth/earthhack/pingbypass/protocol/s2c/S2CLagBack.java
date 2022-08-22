package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SConfirmLagBack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class S2CLagBack extends S2CPacket implements Globals {
    private int id;

    public S2CLagBack() {
        super(ProtocolIds.S2C_LAG_BACK);
    }

    public S2CLagBack(int id) {
        super(ProtocolIds.S2C_LAG_BACK);
        this.id = id;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.id = buf.readVarInt();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.id);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> NetworkUtil.send(new C2SConfirmLagBack(this.id)));
    }

}
