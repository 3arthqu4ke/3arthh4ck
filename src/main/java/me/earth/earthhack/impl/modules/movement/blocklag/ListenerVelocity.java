package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerVelocity extends
        ModuleListener<BlockLag, PacketEvent.Receive<SPacketEntityVelocity>>
{
    public ListenerVelocity(BlockLag module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event)
    {
        if (!module.scaleVelocity.getValue())
        {
            return;
        }

        EntityPlayerSP playerSP = mc.player;
        if (playerSP != null
                && event.getPacket().getEntityID() == playerSP.getEntityId())
        {
            module.motionY = event.getPacket().getMotionY() / 8000.0;
            module.scaleTimer.reset();
        }
    }

}
