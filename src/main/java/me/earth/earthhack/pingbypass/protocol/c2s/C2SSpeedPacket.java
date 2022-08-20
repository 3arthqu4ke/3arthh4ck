package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SSpeedPacket extends C2SPacket implements Globals {
    private boolean collidedHorizontally;
    private boolean collidedVertically;
    private double x;
    private double y;
    private double z;

    public C2SSpeedPacket() {
        super(ProtocolIds.C2S_SPEED);
    }

    public C2SSpeedPacket(boolean collidedHorizontally,
                          boolean collidedVertically,
                          double x, double y, double z) {
        super(ProtocolIds.C2S_SPEED);
        this.collidedHorizontally = collidedHorizontally;
        this.collidedVertically = collidedVertically;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.collidedHorizontally = buf.readBoolean();
        this.collidedVertically = buf.readBoolean();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeBoolean(collidedHorizontally);
        buf.writeBoolean(collidedVertically);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        mc.addScheduledTask(() -> {
            if (mc.player != null) {
                mc.player.collidedHorizontally = collidedHorizontally;
                mc.player.collidedVertically = collidedVertically;
                mc.player.motionX = x;
                mc.player.motionY = y;
                mc.player.motionZ = z;
            }
        });
    }

}
