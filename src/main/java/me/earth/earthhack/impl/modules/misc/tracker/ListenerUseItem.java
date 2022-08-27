package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;

final class ListenerUseItem extends
        ModuleListener<Tracker, PacketEvent.Post<CPacketPlayerTryUseItem>>
{
    public ListenerUseItem(Tracker module)
    {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItem> event)
    {
        if (mc.player.getHeldItem(event.getPacket().getHand())
                     .getItem() == Items.EXPERIENCE_BOTTLE)
        {
            module.awaitingExp.incrementAndGet();
        }
    }

}
