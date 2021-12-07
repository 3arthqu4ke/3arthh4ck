package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerBlockMulti extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketMultiBlockChange>>
{
    public ListenerBlockMulti(Packets module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event)
    {
        if (module.fastBlockStates.getValue())
        {
            SPacketMultiBlockChange p = event.getPacket();
            for (SPacketMultiBlockChange.BlockUpdateData d :
                    p.getChangedBlocks())
            {
                module.stateMap.put(d.getPos(), d.getBlockState());
            }

            mc.addScheduledTask(() ->
            {
                for (SPacketMultiBlockChange.BlockUpdateData d
                        : p.getChangedBlocks())
                {
                    module.stateMap.remove(d.getPos());
                }
            });
        }
    }

}

