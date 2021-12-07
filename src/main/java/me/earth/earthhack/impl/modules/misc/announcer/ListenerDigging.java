package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayerDigging;

final class ListenerDigging extends
        ModuleListener<Announcer, PacketEvent.Post<CPacketPlayerDigging>>
{
    public ListenerDigging(Announcer module)
    {
        super(module, PacketEvent.Post.class, CPacketPlayerDigging.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerDigging> event)
    {
        if (module.mine.getValue())
        {
            CPacketPlayerDigging p = event.getPacket();
            if (p.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
            {
                Block block = mc.world.getBlockState(p.getPosition())
                                      .getBlock();

                module.addWordAndIncrement(AnnouncementType.Mine,
                                           block.getLocalizedName());
            }
        }
    }

}
