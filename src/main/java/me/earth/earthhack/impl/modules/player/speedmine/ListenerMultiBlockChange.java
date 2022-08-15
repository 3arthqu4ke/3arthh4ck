package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerMultiBlockChange extends
        ModuleListener<Speedmine, PacketEvent.Receive<SPacketMultiBlockChange>>
{
    public ListenerMultiBlockChange(Speedmine module)
    {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event)
    {
        SPacketMultiBlockChange packet = event.getPacket();
        if (module.mode.getValue() == MineMode.Fast) {
            for (SPacketMultiBlockChange.BlockUpdateData data
                : packet.getChangedBlocks()) {
                module.fastHelper.onBlockChange(data.getPos(),
                                                data.getBlockState());
            }

            return;
        }

        if ((module.mode.getValue() != MineMode.Smart || module.sentPacket)
                && module.mode.getValue() != MineMode.Instant
                && module.mode.getValue() != MineMode.Civ)
        {
            for (SPacketMultiBlockChange.BlockUpdateData data :
                                                    packet.getChangedBlocks())
            {
                if (data.getPos().equals(module.pos)
                        && data.getBlockState().getBlock() == Blocks.AIR)
                {
                    mc.addScheduledTask(module::reset);
                }
            }
        }
    }

}
