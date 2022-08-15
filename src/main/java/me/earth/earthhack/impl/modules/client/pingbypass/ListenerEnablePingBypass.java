package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketCustomPayload;

import java.util.concurrent.TimeUnit;

public class ListenerEnablePingBypass extends ModuleListener<PingBypassModule, PacketEvent.Receive<SPacketCustomPayload>>
{
    private long lastRequest = 0;

    public ListenerEnablePingBypass(PingBypassModule module)
    {
        super(module, PacketEvent.Receive.class, SPacketCustomPayload.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketCustomPayload> event)
    {
        // TODO: make it so servers cant troll us
        if ("PingBypass|Enable".equals(event.getPacket().getChannelName()))
        {
            event.setCancelled(true);
            if (module.allowEnable.getValue() && !module.isEnabled()
                && TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - lastRequest) > 5)
            {
                lastRequest = System.nanoTime();
                module.protocol.setValue(PbProtocol.New, false);
                module.shouldDisconnect = false;
                module.enable();
                module.shouldDisconnect = true;
            }
        }
    }

}
