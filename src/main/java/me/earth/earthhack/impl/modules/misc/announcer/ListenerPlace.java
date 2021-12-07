package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

final class ListenerPlace extends ModuleListener<Announcer,
        PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>>
{
    public ListenerPlace(Announcer module)
    {
        super(module,
                PacketEvent.Post.class,
                CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event)
    {
        if (module.place.getValue())
        {
            CPacketPlayerTryUseItemOnBlock packet = event.getPacket();
            ItemStack stack = mc.player.getHeldItem(packet.getHand());
            if (stack.getItem() instanceof ItemBlock
                    || stack.getItem() instanceof ItemEndCrystal)
            {
                module.addWordAndIncrement(AnnouncementType.Place,
                                           stack.getDisplayName());
            }
        }
    }

}
