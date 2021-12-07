package me.earth.earthhack.impl.modules.client.server;

import me.earth.earthhack.impl.event.events.misc.AbortEatingEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.server.protocol.Protocol;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import net.minecraft.item.ItemFood;

import java.io.IOException;

final class ListenerStopEating extends
        ModuleListener<ServerModule, AbortEatingEvent>
{
    public ListenerStopEating(ServerModule module)
    {
        super(module, AbortEatingEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(AbortEatingEvent event)
    {
        if (module.currentMode == ServerMode.Client
            || !module.sync.getValue()
            || !module.isEating
            || !(mc.player.getActiveItemStack().getItem() instanceof ItemFood))
        {
            return;
        }

        module.isEating = false;
        byte[] packet = new byte[9];
        ProtocolUtil.addInt(Protocol.EATING, packet);
        ProtocolUtil.addInt(1, packet, 4);
        packet[8] = 0;
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
