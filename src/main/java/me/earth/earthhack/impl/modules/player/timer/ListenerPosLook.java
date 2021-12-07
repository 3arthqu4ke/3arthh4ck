package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook extends
        ModuleListener<Timer, PacketEvent.Receive<SPacketPlayerPosLook>>
{
    public ListenerPosLook(Timer module)
    {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event)
    {
        module.packets = 0;
        module.sent    = 0;
        module.pSpeed  = 1.0f;
    }

}
