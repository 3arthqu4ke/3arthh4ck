package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.ArmUtil;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;

import java.io.IOException;

public class S2CSwingPacket extends S2CPacket implements Globals {
    private EnumHand hand;

    public S2CSwingPacket() {
        super(ProtocolIds.S2C_SWING);
    }

    public S2CSwingPacket(EnumHand hand) {
        super(ProtocolIds.S2C_SWING);
        this.hand = hand;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        hand = buf.readEnumValue(EnumHand.class);
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(hand);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> {
            if (mc.player != null && hand != null) {
                ArmUtil.swingArmNoPacket(hand);
            }
        });
    }

}
