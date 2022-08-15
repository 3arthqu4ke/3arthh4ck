package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;

final class ListenerBlockChange extends
        ModuleListener<Speedmine, PacketEvent.Receive<SPacketBlockChange>>
{
    public ListenerBlockChange(Speedmine module)
    {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event)
    {
        SPacketBlockChange packet = event.getPacket();
        if (module.mode.getValue() == MineMode.Fast) {
            module.fastHelper.onBlockChange(packet.getBlockPosition(),
                                            packet.getBlockState());
            return;
        }

        if (packet.getBlockPosition().equals(module.pos)
            && packet.getBlockState().getBlock() == Blocks.AIR
            && (module.mode.getValue() != MineMode.Smart
            || module.sentPacket)
            && module.mode.getValue() != MineMode.Instant
            && module.mode.getValue() != MineMode.Civ)
        {
            mc.addScheduledTask(module::reset);
        }
        else if (packet.getBlockPosition().equals(module.pos)
            && packet.getBlockState() == mc.world.getBlockState(module.pos)
            && module.shouldAbort
            && module.mode.getValue() == MineMode.Instant)
        {
            mc.player.connection.sendPacket(
                new CPacketPlayerDigging(CPacketPlayerDigging
                                             .Action
                                             .START_DESTROY_BLOCK,
                                         module.pos,
                                         EnumFacing.DOWN));
            module.shouldAbort = false;
        }
    }

}
