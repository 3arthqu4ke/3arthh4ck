package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.client.pbteleport.PbTeleport;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.util.MotionUpdateHelper;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

public class C2SNoMotionUpdateEvent extends C2SPacket implements Globals {
    public C2SNoMotionUpdateEvent() {
        super(ProtocolIds.C2S_NO_MOTION_UPDATE);
    }

    @Override
    public void readInnerBuffer(PacketBuffer buffer) {

    }

    @Override
    public void writeInnerBuffer(PacketBuffer buffer) {

    }

    @Override
    public void execute(NetworkManager networkManager) {
        if (PbTeleport.isBlocking()) {
            if (PbTeleport.shouldPerformMotionUpdate()) {
                MotionUpdateHelper.makeMotionUpdate(
                    PbTeleport.shouldSpoofRotations());
            }

            return;
        }

        mc.addScheduledTask(() -> {
            if (mc.player != null) {
                double x = mc.player.posX;
                double y = mc.player.posY;
                double z = mc.player.posZ;
                float yaw = mc.player.rotationYaw;
                float pit = mc.player.rotationPitch;
                boolean onGround = mc.player.onGround;
                MotionUpdateHelper.makeMotionUpdate(
                    x, y, z, yaw, pit, onGround, true);
            }
        });
    }

}
