package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.pingbypass.packets.PayloadManager;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.event.S2CCustomPacketEvent;
import me.earth.earthhack.pingbypass.protocol.PbPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolFactory;
import me.earth.earthhack.pingbypass.protocol.ProtocolFactoryImpl;
import net.minecraft.network.play.server.SPacketCustomPayload;

import java.io.IOException;

final class ListenerCustomPayload extends ModuleListener<
    PingBypassModule, PacketEvent.Receive<SPacketCustomPayload>>
{
    private final ProtocolFactory factory = new ProtocolFactoryImpl();
    private final PayloadManager manager;

    public ListenerCustomPayload(PingBypassModule module, PayloadManager manager)
    {
        super(module, PacketEvent.Receive.class, SPacketCustomPayload.class);
        this.manager = manager;
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketCustomPayload> event)
    {
        if (event.getPacket().getChannelName().equalsIgnoreCase("PingBypass"))
        {
            if ((!event.isPingBypassCancelled() || !event.isCancelled()) && PingBypass.isServer()) {
                Earthhack.getLogger().warn("Received unexpected PingBypass CustomPayload!");
                event.setCancelled(true);
                event.setPingBypassCancelled(true);
                return;
            }

            try
            {
                event.setCancelled(true);
                if (module.isOld())
                {
                    manager.onPacket(event.getPacket());
                }
                else
                {
                    PbPacket<?> packet = factory.convert(event.getPacket().getBufferData());
                    Event customEvent = new S2CCustomPacketEvent<>(packet);
                    Bus.EVENT_BUS.post(customEvent, packet.getClass());
                    if (!customEvent.isCancelled()) {
                        packet.execute(event.getNetworkManager());
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                BufferUtil.releaseBuffer(event.getPacket().getBufferData());
            }
        }
    }

}
