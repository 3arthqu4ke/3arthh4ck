package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketExplosion;

final class ListenerExplosion extends
        ModuleListener<BlockLag, PacketEvent.Receive<SPacketExplosion>>
{
    public ListenerExplosion(BlockLag module)
    {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event)
    {
        if (module.scaleExplosion.getValue())
        {
            module.motionY = event.getPacket().getMotionY();
            module.scaleTimer.reset();
        }
    }

}
