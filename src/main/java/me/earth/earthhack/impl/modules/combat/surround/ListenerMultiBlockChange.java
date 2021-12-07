package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerMultiBlockChange extends
        ModuleListener<Surround, PacketEvent.Receive<SPacketMultiBlockChange>>
{
    public ListenerMultiBlockChange(Surround module)
    {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event)
    {
        SPacketMultiBlockChange packet = event.getPacket();
        event.addPostEvent(() ->
        {
            boolean instant = false;
            for (SPacketMultiBlockChange.BlockUpdateData data :
                    packet.getChangedBlocks())
            {
                if (module.targets.contains(data.getPos()))
                {
                    if (data.getBlockState().getBlock() == Blocks.AIR)
                    {
                        module.confirmed.remove(data.getPos());
                        if (module.shouldInstant(false) && !instant)
                        {
                            instant = true;
                            ListenerMotion.start(module);
                        }
                    }
                    else if (!data.getBlockState()
                                  .getMaterial()
                                  .isReplaceable())
                    {
                        module.confirmed.add(data.getPos());
                    }
                }
            }
        });
    }

}
