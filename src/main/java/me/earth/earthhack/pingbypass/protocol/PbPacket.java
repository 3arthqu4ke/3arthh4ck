package me.earth.earthhack.pingbypass.protocol;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public interface PbPacket<T extends INetHandler> extends Packet<T> {
    void readInnerBuffer(PacketBuffer buf) throws IOException;

    void writeInnerBuffer(PacketBuffer buf) throws IOException;

    void execute(NetworkManager networkManager) throws IOException;

}
