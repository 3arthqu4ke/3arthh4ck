package me.earth.earthhack.impl.modules.misc.nuker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

final class ListenerChange extends
        ModuleListener<Nuker, PacketEvent.Receive<SPacketBlockChange>>
{
    public ListenerChange(Nuker module)
    {
        super(module, PacketEvent.Receive.class, 11, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event)
    {
        if (module.instant.getValue()
                && module.rotate.getValue() != Rotate.Normal)
        {
            SPacketBlockChange packet = event.getPacket();
            Set<Block> blocks = module.getBlocks();

            if (blocks.isEmpty())
            {
                return;
            }

            if (blocks.contains(packet.getBlockState().getBlock())
                    && BlockUtil
                        .getDistanceSqDigging(packet.getBlockPosition())
                            <= MathUtil.square(module.range.getValue()))
            {
                mc.addScheduledTask(() ->
                {
                    if (mc.player.getActiveItemStack().getItem()
                            instanceof ItemFood)
                    {
                        return;
                    }

                    BlockPos pos = packet.getBlockPosition();
                    int slot = MineUtil.findBestTool(pos);

                    if (slot != -1)
                    {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                        {
                            int lastSlot = mc.player.inventory.currentItem;
                            module.timer.reset();
                            InventoryUtil.switchTo(slot);
                            module.attack(pos);
                            InventoryUtil.switchTo(lastSlot);
                        });
                    }
                });
            }
        }
    }

}
