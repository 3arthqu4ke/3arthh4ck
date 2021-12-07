package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange extends
            ModuleListener<PistonAura, PacketEvent.Receive<SPacketBlockChange>>
{
    public ListenerBlockChange(PistonAura module)
    {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event)
    {
        if (!module.change.getValue())
        {
            return;
        }

        mc.addScheduledTask(() ->
        {
            if (module.current != null)
            {
                SPacketBlockChange packet = event.getPacket();
                if (module.checkUpdate(packet.getBlockPosition(),
                                       packet.getBlockState(),
                                       module.current.getRedstonePos(),
                                       Blocks.REDSTONE_BLOCK,
                                       Blocks.REDSTONE_TORCH)
                        || module.checkUpdate(packet.getBlockPosition(),
                                              packet.getBlockState(),
                                              module.current.getPistonPos(),
                                              Blocks.PISTON,
                                              Blocks.STICKY_PISTON))
                {
                    module.current.setValid(false);
                }
            }
        });
    }

}
