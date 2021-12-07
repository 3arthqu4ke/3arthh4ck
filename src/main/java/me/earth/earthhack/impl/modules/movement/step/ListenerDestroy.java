package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketPlayerDigging;

final class ListenerDestroy extends
        ModuleListener<Step, PacketEvent.Post<CPacketPlayerDigging>>
{
    public ListenerDestroy(Step module)
    {
        super(module, PacketEvent.Post.class, CPacketPlayerDigging.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerDigging> event)
    {
        if (event.getPacket().getAction() ==
                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
        {
            module.onBreak();
        }
    }

}
