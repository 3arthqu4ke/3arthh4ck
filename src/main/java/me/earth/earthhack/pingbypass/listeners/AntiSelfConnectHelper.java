package me.earth.earthhack.pingbypass.listeners;

import net.minecraft.network.PacketBuffer;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Prevents a PingBypass server from connecting to itself.
 */
public class AntiSelfConnectHelper {
    private static final AtomicReference<byte[]> CURRENT = new AtomicReference<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    public static PacketBuffer generate(PacketBuffer buf) {
        byte[] randomBytes = new byte[16];
        RANDOM.nextBytes(randomBytes);
        buf.writeBytes(randomBytes);
        CURRENT.set(randomBytes);
        return buf;
    }

    public static byte[] getCurrent() {
        return CURRENT.get();
    }

}
