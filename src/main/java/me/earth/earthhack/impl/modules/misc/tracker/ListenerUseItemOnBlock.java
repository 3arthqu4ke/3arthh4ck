package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;

final class ListenerUseItemOnBlock extends
    ModuleListener<Tracker, PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>>
{
    public ListenerUseItemOnBlock(Tracker module)
    {
        super(module,
                PacketEvent.Post.class,
                CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event)
    {
        CPacketPlayerTryUseItemOnBlock packet = event.getPacket();
        if (mc.player.getHeldItem(packet.getHand())
                     .getItem() == Items.END_CRYSTAL)
        {
            BlockPos pos = packet.getPos();
            module.placed.add(new BlockPos(pos.getX() + 0.5f,
                                           pos.getY() + 1,
                                           pos.getZ() + 0.5f));
        }
    }

}
