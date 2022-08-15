package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketPlayer;

import java.io.IOException;

public class C2SStepPacket extends C2SPacket implements Globals {
    private double[] offsets;
    private double x;
    private double y;
    private double z;

    public C2SStepPacket() {
        super(ProtocolIds.C2S_STEP);
    }

    public C2SStepPacket(double[] offsets, double x, double y, double z) {
        super(ProtocolIds.C2S_STEP);
        this.offsets = offsets;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        int length = buf.readVarInt();
        double[] offsets = new double[length];
        for (int i = 0; i < length; i++) {
            offsets[i] = buf.readDouble();
        }

        this.offsets = offsets;
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeVarInt(offsets.length);
        for (double offset : offsets) {
            buf.writeDouble(offset);
        }
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> {
            if (mc.player != null) {
                for (double offset : offsets) {
                    PingBypass.sendToActualServer(
                        new CPacketPlayer.Position(x, y + offset, z, true));
                }
            }
        });
    }

}
