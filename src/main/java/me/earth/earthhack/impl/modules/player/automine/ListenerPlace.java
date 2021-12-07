package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

final class ListenerPlace extends
    ModuleListener<AutoMine, PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>>
{
    public ListenerPlace(AutoMine module)
    {
        super(module,
                PacketEvent.Post.class,
                CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event)
    {
        if (mc.player.getHeldItem(event.getPacket().getHand())
                     .getItem() == Items.END_CRYSTAL)
        {
            module.downTimer.reset();
        }
    }

}
