package me.earth.earthhack.impl.modules.client.server;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.server.protocol.Protocol;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;

import java.io.IOException;

final class ListenerStartEating extends
        ModuleListener<ServerModule, PacketEvent.Send<CPacketPlayerTryUseItem>>
{
    public ListenerStartEating(ServerModule module)
    {
        super(module,
                PacketEvent.Send.class,
                Integer.MIN_VALUE,
                CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItem> event)
    {
        if (event.isCancelled()
            || module.currentMode == ServerMode.Client
            || !module.sync.getValue()
            || !(mc.player.getHeldItem(event.getPacket().getHand())
                          .getItem() instanceof ItemFood))
        {
            return;
        }

        module.isEating = true;
        byte[] packet = new byte[9];
        ProtocolUtil.addInt(Protocol.EATING, packet);
        ProtocolUtil.addInt(1, packet, 4);
        packet[8] = Byte.MIN_VALUE;
        try
        {
            module.connectionManager.send(packet);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
