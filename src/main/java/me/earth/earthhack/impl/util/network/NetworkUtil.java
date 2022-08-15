package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.INetworkManager;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;

public class NetworkUtil implements Globals
{
    /**
     * Sends the given Packet safely (Packet won't be send
     * if {@link Minecraft#getConnection()} is <tt>null</tt>.
     *
     * @param packet the packet to send.
     */
    public static void send(Packet<?> packet)
    {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null)
        {
            connection.sendPacket(packet);
        }
    }

    /**
     * Convenience Method, calls
     * {@link INetworkManager#sendPacketNoEvent(Packet)}.
     *
     * @param packet the packet to send.
     * @return the packet or null if failed.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Packet<?> sendPacketNoEvent(Packet<?> packet)
    {
        return sendPacketNoEvent(packet, true);
    }

    /**
     * Convenience Method, calls
     * {@link INetworkManager#sendPacketNoEvent(Packet, boolean)}.
     *
     * @param packet the packet to send.
     * @param post if a post event should be send.
     * @return the packet or null if failed.
     */
    public static Packet<?> sendPacketNoEvent(Packet<?> packet, boolean post)
    {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null)
        {
            INetworkManager manager =
                    (INetworkManager) connection.getNetworkManager();

            return manager.sendPacketNoEvent(packet, post);
        }

        return null;
    }

    public static boolean receive(Packet<INetHandlerPlayClient> packet)
    {
        EntityPlayerSP player = mc.player;
        if (player != null) {
            return receive(packet, player.connection.getNetworkManager());
        }

        return false;
    }

    public static boolean receive(Packet<INetHandlerPlayClient> packet, NetworkManager manager)
    {
        PacketEvent.Receive<?> e = new PacketEvent.Receive<>(packet, manager);
        Bus.EVENT_BUS.post(e, packet.getClass());
        if (e.isCancelled())
        {
            return false;
        }

        packet.processPacket(mc.player.connection);

        for (Runnable runnable : e.getPostEvents())
        {
            runnable.run();
        }

        return true;
    }

}
