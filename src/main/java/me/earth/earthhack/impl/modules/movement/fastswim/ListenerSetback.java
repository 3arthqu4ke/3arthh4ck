package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class ListenerSetback
        extends ModuleListener<FastSwim, PacketEvent.Receive<SPacketPlayerPosLook>>
{

    public ListenerSetback(FastSwim module)
    {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event)
    {
        module.waterSpeed = module.hWater.getValue();
        module.lavaSpeed = module.hLava.getValue();
    }

}
