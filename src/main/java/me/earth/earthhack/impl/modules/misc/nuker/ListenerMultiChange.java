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
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

final class ListenerMultiChange extends
        ModuleListener<Nuker, PacketEvent.Receive<SPacketMultiBlockChange>>
{
    public ListenerMultiChange(Nuker module)
    {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event)
    {
        if (module.instant.getValue()
                && module.rotate.getValue() != Rotate.Normal)
        {
            SPacketMultiBlockChange packet = event.getPacket();
            Set<BlockPos> toAttack = new HashSet<>();
            Set<Block> blocks = module.getBlocks();

            if (blocks.isEmpty())
            {
                return;
            }

            for (SPacketMultiBlockChange.BlockUpdateData data :
                    packet.getChangedBlocks())
            {
                if (blocks.contains(data.getBlockState().getBlock())
                        && BlockUtil
                            .getDistanceSqDigging(mc.player, data.getPos())
                                <= MathUtil.square(module.range.getValue()))
                {
                    toAttack.add(data.getPos());
                }
            }

            if (!toAttack.isEmpty())
            {
                mc.addScheduledTask(() ->
                {
                    if (mc.player.getActiveItemStack().getItem()
                            instanceof ItemFood)
                    {
                        return;
                    }
                    //noinspection OptionalGetWithoutIsPresent
                    BlockPos pos = toAttack.stream().findFirst().get();
                    int slot = MineUtil.findBestTool(pos);

                    if (slot != -1)
                    {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                        {
                            int lastSlot = mc.player.inventory.currentItem;
                            InventoryUtil.switchTo(slot);
                            for (BlockPos p : toAttack)
                            {
                                module.attack(p);
                            }
                            InventoryUtil.switchTo(lastSlot);
                        });
                    }
                });
            }
        }
    }

}
