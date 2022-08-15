package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;

public class C2SCommandPacket extends C2SPacket {
    private String[] args;
    private int length;

    public C2SCommandPacket() {
        super(ProtocolIds.C2S_COMMAND);
    }

    public C2SCommandPacket(String... args) {
        super(ProtocolIds.C2S_COMMAND);
        this.args = args;
        this.length = args.length;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) {
        length = buf.readVarInt();
        args = new String[length];
        for (int i = 0; i < length; i++) {
            args[i] = buf.readString(Short.MAX_VALUE);
        }
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) {
        buf.writeVarInt(length);
        for (String string : args) {
            buf.writeString(string);
        }
    }

    @Override
    public void execute(NetworkManager networkManager) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (args != null) {
                Managers.COMMANDS.executeArgs(args);
            }
        });
    }

}
