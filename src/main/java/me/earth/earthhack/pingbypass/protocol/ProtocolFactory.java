package me.earth.earthhack.pingbypass.protocol;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ProtocolFactory {
    private static final Logger LOGGER = LogManager.getLogger(ProtocolFactory.class);

    private final Map<Integer, Supplier<PbPacket<?>>> factories = new ConcurrentHashMap<>();

    public void register(int id, Supplier<PbPacket<?>> factory) {
        factories.put(id, factory);
    }

    public void handle(PacketBuffer buffer, NetworkManager networkManager) {
        try {
            convert(buffer).execute(networkManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PbPacket<?> convert(PacketBuffer packetBuffer) throws IOException {
        int id = packetBuffer.readVarInt();
        Supplier<PbPacket<?>> factory = factories.get(id);
        if (factory == null) {
            LOGGER.error("Could not find Packet Factory for id " + id);
            throw new IOException("Could not find Packet Factory for id " + id);
        }

        PbPacket<?> packet = factory.get();

        try {
            packet.readInnerBuffer(packetBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return packet;
    }

}
