package me.earth.earthhack.pingbypass.nethandler;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.IC00Handshake;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CPasswordPacket;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.Session;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class LoginHandler extends BaseNetHandler
    implements INetHandlerLoginServer, Globals, IPbNetHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();
    private final byte[] verifyToken = new byte[4];
    private LoginHandler.LoginState currentLoginState = LoginHandler.LoginState.HELLO;
    private GameProfile loginGameProfile;
    // only present if we are supposed to join the server
    private final IC00Handshake handshake;

    public LoginHandler(NetworkManager networkManagerIn, IC00Handshake handshake)
    {
        super(networkManagerIn, 60_000);
        RANDOM.nextBytes(this.verifyToken);
        this.handshake = handshake;
    }

    @Override
    public void update()
    {
        if (this.currentLoginState == LoginHandler.LoginState.READY_TO_ACCEPT)
        {
            this.tryAcceptPlayer();
        }

        super.update();
    }

    @Override
    public void onDisconnect(ITextComponent reason)
    {
        LOGGER.info("{} lost connection: {}", this.getConnectionInfo(), reason.getUnformattedText());
        PingBypass.setConnected(false);
    }

    @Override
    public void processLoginStart(CPacketLoginStart packetIn)
    {
        Validate.validState(this.currentLoginState == LoginHandler.LoginState.HELLO, "Unexpected hello packet");
        this.loginGameProfile = packetIn.getProfile();

        this.currentLoginState = LoginHandler.LoginState.KEY;
        this.networkManager.sendPacket(new SPacketEncryptionRequest("", PingBypass.KEY_PAIR.getPublic(), this.verifyToken));
    }

    @Override
    public void processEncryptionResponse(CPacketEncryptionResponse packetIn)
    {
        Validate.validState(this.currentLoginState == LoginHandler.LoginState.KEY, "Unexpected key packet");
        PrivateKey privatekey = PingBypass.KEY_PAIR.getPrivate();

        if (!Arrays.equals(this.verifyToken, packetIn.getVerifyToken(privatekey)))
        {
            throw new IllegalStateException("Invalid nonce!");
        }
        else
        {
            SecretKey secretKey = packetIn.getSecretKey(privatekey);
            this.currentLoginState = LoginState.READY_TO_ACCEPT;
            this.networkManager.enableEncryption(secretKey);
        }
    }

    @Override
    public String getConnectionInfo()
    {
        return this.loginGameProfile != null ? this.loginGameProfile + " (" + this.networkManager.getRemoteAddress() + ")" : String.valueOf(this.networkManager.getRemoteAddress());
    }

    @SuppressWarnings("unchecked")
    public void tryAcceptPlayer()
    {
        if (!this.loginGameProfile.isComplete())
        {
            this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
        }

        this.currentLoginState = LoginHandler.LoginState.ACCEPTED;
        int threshold = PingBypass.CONFIG.getCompressionThreshold();
        if (threshold >= 0 && !this.networkManager.isLocalChannel())
        {
            this.networkManager.sendPacket(new SPacketEnableCompression(threshold),
                                           (ChannelFutureListener) op -> LoginHandler.this.networkManager.setCompressionThreshold(threshold));
        }

        EntityPlayerSP player = mc.player;
        if (player != null) {
            this.networkManager.sendPacket(new SPacketLoginSuccess(
                new GameProfile(player.getUniqueID(), player.getName())));
        } else {
            Session session = mc.getSession();
            //noinspection ConstantConditions
            if (session != null) {
                this.networkManager.sendPacket(new SPacketLoginSuccess(session.getProfile()));
            } else {
                this.networkManager.sendPacket(new SPacketLoginSuccess(this.loginGameProfile));
            }
        }

        this.networkManager.sendPacket(new SPacketCustomPayload(
            "PingBypass|Enable", new PacketBuffer(Unpooled.buffer())));

        if (PingBypass.CONFIG.noPassword()) {
            PbNetHandler.onLogin(networkManager, handshake);
        } else {
            networkManager.setNetHandler(new PasswordHandler(networkManager, handshake));
            networkManager.sendPacket(new S2CPasswordPacket());
        }
    }

    protected GameProfile getOfflineProfile(GameProfile original)
    {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(StandardCharsets.UTF_8));
        return new GameProfile(uuid, original.getName());
    }

    enum LoginState
    {
        HELLO,
        KEY,
        READY_TO_ACCEPT,
        AWAITING_PASSWORD,
        ACCEPTED,
    }

}
