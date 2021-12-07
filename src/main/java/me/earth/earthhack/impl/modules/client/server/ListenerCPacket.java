package me.earth.earthhack.impl.modules.client.server;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.protocol.Protocol;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolPlayUtil;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketPlayer;

import java.io.IOException;

final class ListenerCPacket extends CPacketPlayerListener
{
    private final ServerModule module;

    public ListenerCPacket(ServerModule module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event)
    {
        onEvent(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event)
    {
        onEvent(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event)
    {
        onEvent(event);
    }

    @Override
    protected void onPositionRotation(
            PacketEvent.Send<CPacketPlayer.PositionRotation> event)
    {
        onEvent(event);
    }

    private void onEvent(PacketEvent.Send<? extends CPacketPlayer> event)
    {
        if (event.isCancelled() || !module.sync.getValue())
        {
            return;
        }

        if (module.currentMode == ServerMode.Client)
        {
            event.setCancelled(true);
            return;
        }

        Packet<?> p = event.getPacket();
        PacketBuffer buffer = null;

        try
        {
            buffer = new PacketBuffer(Unpooled.buffer());
            int id = EnumConnectionState
                    .getFromPacket(p)
                    .getPacketId(EnumPacketDirection.SERVERBOUND, p);
            // Write ID
            buffer.writeInt(Protocol.PACKET);
            // Save index before size for later
            int index = buffer.writerIndex();
            // Placeholder for size
            buffer.writeInt(0xffffffff);
            // Save WriterIndex so we can calculate size
            int size = buffer.writerIndex();
            // Write normal minecraft packet stuff.
            buffer.writeVarInt(id);
            event.getPacket().writePacketData(buffer);
            // Save index so we can jump back.
            int lastIndex = buffer.writerIndex();
            // Calculate size
            size = buffer.writerIndex() - size;
            // Go back to the Index where we want to write the size
            buffer.writerIndex(index);
            // Write Size
            buffer.writeInt(size);
            // Go back to the end.
            buffer.writerIndex(lastIndex);

            byte[] packets = ProtocolPlayUtil.velocityAndPosition(
                    RotationUtil.getRotationPlayer());

            for (IConnection connection :
                    module.connectionManager.getConnections())
            {
                try
                {
                    // write packet to the OutputStream
                    buffer.getBytes(
                            0,
                            connection.getOutputStream(),
                            buffer.readableBytes());

                    connection.send(packets);
                }
                catch (IOException e)
                {
                    module.connectionManager.remove(connection);
                    Earthhack.getLogger().warn(
                            "Error with Connection: " + connection.getName());
                    e.printStackTrace();
                }
            }
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
