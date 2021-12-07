package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.login.client.CPacketLoginStart;

final class ListenerLogin extends ModuleListener<PingBypass, PacketEvent.Send<CPacketLoginStart>>
{
    public ListenerLogin(PingBypass module)
    {
        super(module, PacketEvent.Send.class, CPacketLoginStart.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketLoginStart> event)
    {
        module.friendSerializer.clear();
        module.serializer.clear();
    }

}
