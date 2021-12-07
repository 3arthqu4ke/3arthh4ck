package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

final class ListenerTeleport extends
        ModuleListener<LagOMeter, PacketEvent.Post<CPacketConfirmTeleport>>
{
    public ListenerTeleport(LagOMeter module)
    {
        super(module, PacketEvent.Post.class, CPacketConfirmTeleport.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketConfirmTeleport> event)
    {
        module.teleported.set(true);
    }

}
