package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.EnumHand;

final class ListenerAnimation extends
        ModuleListener<Spectate, PacketEvent.Receive<SPacketAnimation>>
{
    public ListenerAnimation(Spectate module)
    {
        super(module, PacketEvent.Receive.class, SPacketAnimation.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketAnimation> event)
    {
        event.addPostEvent(() ->
        {
            EntityPlayer playerSp = mc.player;
            if (playerSp != null && module.spectating)
            {
                EntityPlayer player = module.player;
                SPacketAnimation packet = event.getPacket();
                if (player != null
                        && packet.getEntityID() == player.getEntityId())
                {
                    if (packet.getAnimationType() == 0)
                    {
                        playerSp.swingArm(EnumHand.MAIN_HAND);
                    }
                    else if (packet.getAnimationType() == 3)
                    {
                        playerSp.swingArm(EnumHand.OFF_HAND);
                    }
                }
            }
        });
    }

}
