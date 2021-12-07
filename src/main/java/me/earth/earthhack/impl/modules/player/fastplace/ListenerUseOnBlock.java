package me.earth.earthhack.impl.modules.player.fastplace;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;

final class ListenerUseOnBlock extends
     ModuleListener<FastPlace, PacketEvent.Send<CPacketPlayerTryUseItemOnBlock>>
{
    public ListenerUseOnBlock(FastPlace module)
    {
        super(module,
                PacketEvent.Send.class,
                CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItemOnBlock> event)
    {
        if (module.bypass.getValue()
                && mc.player.getHeldItem(event.getPacket().getHand())
                            .getItem() == Items.EXPERIENCE_BOTTLE
            || module.foodBypass.getValue()
                && mc.player.getHeldItem(event.getPacket().getHand())
                            .getItem() instanceof ItemFood)
        {
            if (Managers.ACTION.isSneaking()
                || module.bypassContainers.getValue())
            {
                event.setCancelled(true);
            }
            else
            {
                BlockPos pos = event.getPacket().getPos();
                IBlockState state = mc.world.getBlockState(pos);
                if (!SpecialBlocks.BAD_BLOCKS.contains(state.getBlock())
                    && !SpecialBlocks.SHULKERS.contains(state.getBlock()))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

}
