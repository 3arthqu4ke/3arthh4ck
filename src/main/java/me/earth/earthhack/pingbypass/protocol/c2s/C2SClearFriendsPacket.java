package me.earth.earthhack.pingbypass.protocol.c2s;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.pingbypass.protocol.C2SPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class C2SClearFriendsPacket extends C2SPacket {
    private static final Logger LOGGER = LogManager.getLogger(C2SClearFriendsPacket.class);

    public C2SClearFriendsPacket() {
        super(ProtocolIds.C2S_CLEAR_FRIENDS);
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {

    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {

    }

    @Override
    public void execute(NetworkManager networkManager) throws IOException {
        LOGGER.info("Clearing friends...");
        Managers.FRIENDS.clear();
    }

}
