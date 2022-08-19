package me.earth.earthhack.pingbypass.nethandler;

import me.earth.earthhack.impl.core.ducks.network.IC00Handshake;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link net.minecraft.server.network.NetHandlerHandshakeTCP} for PingBypass.
 */
@SuppressWarnings("NullableProblems")
public class HandshakeHandler implements INetHandlerHandshakeServer
{
    private static final Logger LOGGER = LogManager.getLogger(HandshakeHandler.class);

    private final NetworkManager networkManager;

    public HandshakeHandler(NetworkManager netManager)
    {
        this.networkManager = netManager;
    }

    public void processHandshake(C00Handshake packetIn)
    {
        boolean play = false;
        switch (packetIn.getRequestedState())
        {
            case PLAY:
                play = true;
            case LOGIN:
                LOGGER.debug("Received LOGIN Handshake");
                this.networkManager.setConnectionState(EnumConnectionState.LOGIN);
                if (packetIn.getProtocolVersion() > 340)
                {
                    ITextComponent itextcomponent = new TextComponentTranslation("multiplayer.disconnect.outdated_server", "1.12.2");
                    this.networkManager.sendPacket(new SPacketDisconnect(itextcomponent));
                    this.networkManager.closeChannel(itextcomponent);
                }
                else if (packetIn.getProtocolVersion() < 340)
                {
                    ITextComponent itextcomponent = new TextComponentTranslation("multiplayer.disconnect.outdated_client", "1.12.2");
                    this.networkManager.sendPacket(new SPacketDisconnect(itextcomponent));
                    this.networkManager.closeChannel(itextcomponent);
                }
                else
                {
                    if (PingBypass.isConnected())
                    {
                        ITextComponent itextcomponent = new TextComponentString("This PingBypass server is already in use!");
                        this.networkManager.sendPacket(new SPacketDisconnect(itextcomponent));
                        this.networkManager.closeChannel(itextcomponent);
                    }
                    else
                    {
                        this.networkManager.setNetHandler(new LoginHandler(this.networkManager, play ? (IC00Handshake) packetIn : null));
                    }
                }

                break;
            case STATUS:
                LOGGER.debug("Received STATUS Handshake");
                this.networkManager.setConnectionState(EnumConnectionState.STATUS);
                this.networkManager.setNetHandler(new StatusHandler(this.networkManager));
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packetIn.getRequestedState());
        }
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(ITextComponent reason)
    {

    }

}
