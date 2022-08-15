package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.util.CryptManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.security.PublicKey;

/**
 * This does NOT let you play without an account. It just makes it possible to join PingBypass servers from Intellij.
 */
public class DevEnvironmentAuth extends EventListener<PacketEvent.Receive<SPacketEncryptionRequest>> implements Globals {
    public static final boolean DEV_AUTH = Boolean.parseBoolean(System.getProperty("earthhack.dev.auth", "false"));
    private static final Logger LOGGER = LogManager.getLogger(DevEnvironmentAuth.class);

    public DevEnvironmentAuth() {
        super(PacketEvent.Receive.class, SPacketEncryptionRequest.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(PacketEvent.Receive<SPacketEncryptionRequest> event) {
        if (DEV_AUTH && event.getNetworkManager() != null) {
            event.setCancelled(true);
            LOGGER.info("Using DevEnvironmentAuth!");
            SPacketEncryptionRequest packetIn = event.getPacket();
            final SecretKey secretkey = CryptManager.createNewSharedKey();
            PublicKey publickey = packetIn.getPublicKey();
            event.getNetworkManager().sendPacket(
                new CPacketEncryptionResponse(secretkey, publickey, packetIn.getVerifyToken()),
                e -> event.getNetworkManager().enableEncryption(secretkey));
        }
    }

}
