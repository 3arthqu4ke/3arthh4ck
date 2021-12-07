package me.earth.earthhack.impl.modules.misc.buildheight;

import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayerTryUseItemOnBlock;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;

final class ListenerPlaceBlock extends
        ModuleListener<BuildHeight,
                PacketEvent.Send<CPacketPlayerTryUseItemOnBlock>>
{
    public ListenerPlaceBlock(BuildHeight module)
    {
        super(module,
                PacketEvent.Send.class,
                CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItemOnBlock> event)
    {
        CPacketPlayerTryUseItemOnBlock packet = event.getPacket();
        if (packet.getPos().getY() >= 255
                && (!module.crystals.getValue() ||
                     mc.player.getHeldItem(packet.getHand()).getItem()
                        == Items.END_CRYSTAL)
                && packet.getDirection() == EnumFacing.UP)
        {
            ((ICPacketPlayerTryUseItemOnBlock) packet)
                    .setFacing(EnumFacing.DOWN);
        }
    }

}
