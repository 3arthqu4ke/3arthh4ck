package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketAnimation;

final class ListenerAnimation extends
        ModuleListener<NoRender, PacketEvent.Receive<SPacketAnimation>>
{
    public ListenerAnimation(NoRender module)
    {
        super(module, PacketEvent.Receive.class, SPacketAnimation.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketAnimation> event)
    {
        if (module.critParticles.getValue()
                && (event.getPacket().getAnimationType() == 4
                    || event.getPacket().getAnimationType() == 5))
        {
            event.setCancelled(true);
        }
    }

}
