package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class S2CWorldTickPacket extends S2CPacket implements Globals {
    public S2CWorldTickPacket() {
        super(ProtocolIds.S2C_WORLD_TICK);
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {

    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {

    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> {
            if (mc.world != null) {
                mc.world.tick();
            }
        });
    }

}
