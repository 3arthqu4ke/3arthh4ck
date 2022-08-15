package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class S2CPositionPacket extends S2CPacket implements Globals {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public S2CPositionPacket() {
        super(ProtocolIds.S2C_POSITION);
    }

    public S2CPositionPacket(double x, double y, double z, float yaw,
                             float pitch, boolean onGround) {
        super(ProtocolIds.S2C_POSITION);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.onGround = buf.readBoolean();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeBoolean(onGround);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> {
            if (mc.player != null) {
                mc.player.setPositionAndRotation(x, y, z, yaw, pitch);
                mc.player.onGround = onGround;
                PacketUtil.loadTerrain();
            } else {
                ChatUtil.sendMessage("S2CPosition but player was null!!!");
            }
        });
    }

}
