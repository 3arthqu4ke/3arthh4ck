package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;

public class C2SStayPacket extends C2SPacket {
    public C2SStayPacket() {
        super(ProtocolIds.C2S_STAY);
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {

    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(NetworkManager networkManager) throws IOException {
        NetworkManager manager = PingBypass.getNetworkManager();
        if (manager != null) {
            PingBypass.setStay(true);
            TextComponentString reason = new TextComponentString("Quitting, PingBypass will stay connected.");
            manager.sendPacket(new SPacketDisconnect(reason), o -> manager.closeChannel(reason));
        }
    }

}
