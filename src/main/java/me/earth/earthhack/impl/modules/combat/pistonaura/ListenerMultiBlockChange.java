package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerMultiBlockChange extends
        ModuleListener<PistonAura, PacketEvent.Receive<SPacketMultiBlockChange>>
{
    public ListenerMultiBlockChange(PistonAura module)
    {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event)
    {
        if (!module.multiChange.getValue())
        {
            return;
        }

        mc.addScheduledTask(() ->
        {
            if (module.current != null)
            {
                SPacketMultiBlockChange packet = event.getPacket();
                for (SPacketMultiBlockChange.BlockUpdateData data :
                                                    packet.getChangedBlocks())
                {
                    if (module.checkUpdate(data.getPos(),
                                           data.getBlockState(),
                                           module.current.getRedstonePos(),
                                           Blocks.REDSTONE_BLOCK,
                                           Blocks.REDSTONE_TORCH)
                        || module.checkUpdate(data.getPos(),
                                              data.getBlockState(),
                                              module.current.getPistonPos(),
                                              Blocks.PISTON,
                                              Blocks.STICKY_PISTON))
                    {
                        module.current.setValid(false);
                        return;
                    }
                }
            }
        });
    }

}
