package me.earth.earthhack.impl.modules.client.server.protocol.handlers;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.ILogger;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;
import me.earth.earthhack.impl.modules.client.server.protocol.CopyPacket;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class PacketHandler implements IPacketHandler, Globals
{
    private final ILogger logger;

    public PacketHandler(ILogger logger)
    {
        this.logger = logger;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes)
    {
        EntityPlayerSP playerSP = mc.player;
        if (playerSP == null)
        {
            logger.log("Received a packet, but we are not ingame!");
            return;
        }

        PacketBuffer buffer = null;

        try
        {
            buffer = new PacketBuffer(Unpooled.wrappedBuffer(bytes));
            int id = buffer.readVarInt();
            logger.log("Received Packet with ID: " + id);
            Packet<?> packet = new CopyPacket(
                    id,
                    EnumConnectionState.PLAY.ordinal(),
                    bytes,
                    buffer.readerIndex());
            playerSP.connection.sendPacket(packet);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (buffer != null)
            {
                BufferUtil.releaseBuffer(buffer);
            }
        }
    }

}
