package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.login.server.SPacketLoginSuccess;

final class ListenerLogin extends ModuleListener<PingBypassModule, PacketEvent.Receive<SPacketLoginSuccess>>
{
    public ListenerLogin(PingBypassModule module)
    {
        super(module, PacketEvent.Receive.class, SPacketLoginSuccess.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketLoginSuccess> event)
    {
        module.friendSerializer.clear();
        module.serializer.clear();
    }

}
