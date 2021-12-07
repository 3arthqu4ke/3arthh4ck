package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook extends
        ModuleListener<LongJump, PacketEvent.Receive<SPacketPlayerPosLook>>
{
    public ListenerPosLook(LongJump module)
    {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event)
    {
        if (module.noKick.getValue())
        {
            mc.addScheduledTask(module::disable);
        }

        module.speed       = 0.0;
        module.stage       = 0;
        module.airTicks    = 0;
        module.groundTicks = 0;
    }

}
