package me.earth.earthhack.pingbypass.nethandler;

import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.status.INetHandlerStatusServer;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@SuppressWarnings("NullableProblems")
public class StatusHandler implements INetHandlerStatusServer
{
    private static final ITextComponent EXIT_MESSAGE = new TextComponentString("Status request has been handled.");
    private final NetworkManager networkManager;
    private boolean handled;

    public StatusHandler(NetworkManager netManager)
    {
        this.networkManager = netManager;
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(ITextComponent reason)
    {
    }

    public void processServerQuery(CPacketServerQuery packetIn)
    {
        if (this.handled)
        {
            this.networkManager.closeChannel(EXIT_MESSAGE);
        }
        else
        {
            this.handled = true;
            this.networkManager.sendPacket(new SPacketServerInfo(PingBypass.INFO.getResponse()));
        }
    }

    public void processPing(CPacketPing packetIn)
    {
        this.networkManager.sendPacket(new SPacketPong(packetIn.getClientTime()));
        this.networkManager.closeChannel(EXIT_MESSAGE);
    }

}
