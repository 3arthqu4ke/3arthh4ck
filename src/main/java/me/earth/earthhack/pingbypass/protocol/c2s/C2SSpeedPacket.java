package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SSpeedPacket extends C2SPacket implements Globals {
    private double x;
    private double y;
    private double z;

    public C2SSpeedPacket() {
        super(ProtocolIds.C2S_SPEED);
    }

    public C2SSpeedPacket(double x, double y, double z) {
        super(ProtocolIds.C2S_SPEED);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> {
            if (mc.player != null) {
                mc.player.motionX = x;
                mc.player.motionY = y;
                mc.player.motionZ = z;
            }
        });
    }

}
