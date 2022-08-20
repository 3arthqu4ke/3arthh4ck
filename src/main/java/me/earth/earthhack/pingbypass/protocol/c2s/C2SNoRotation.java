package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.pbteleport.PbTeleport;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.util.MotionUpdateHelper;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class C2SNoRotation extends C2SPacket implements Globals {
    private boolean onGround;
    private double x;
    private double y;
    private double z;

    public C2SNoRotation() {
        super(ProtocolIds.C2S_NO_ROTATION);
    }

    public C2SNoRotation(double x, double y, double z, boolean onGround) {
        super(ProtocolIds.C2S_NO_ROTATION);
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.onGround = buf.readBoolean();
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeBoolean(onGround);
    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        if (PbTeleport.isBlocking()) {
            if (PbTeleport.shouldPerformMotionUpdate()) {
                MotionUpdateHelper.makeMotionUpdate(
                    PbTeleport.shouldSpoofRotations());
            }

            return;
        }

        mc.addScheduledTask(() -> {
            if (mc.player != null) {
                MotionUpdateHelper.makeMotionUpdate(
                    x, y, z, Managers.ROTATION.getServerYaw(),
                    Managers.ROTATION.getServerPitch(), onGround, false);
            }
        });
    }

}
