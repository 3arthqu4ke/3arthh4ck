package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerVelocity extends
        ModuleListener<HighJump, PacketEvent.Receive<SPacketEntityVelocity>>
{
    public ListenerVelocity(HighJump module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event)
    {
        EntityPlayerSP player = mc.player;
        SPacketEntityVelocity packet = event.getPacket();
        if (module.velocity.getValue()
                && player != null
                && player.getEntityId() == packet.getEntityID())
        {
            double y = packet.getMotionY() / 8000.0;
            module.addVelocity(y);
        }
    }

}
