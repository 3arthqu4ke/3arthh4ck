package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange extends
        ModuleListener<Surround, PacketEvent.Receive<SPacketBlockChange>>
{
    public ListenerBlockChange(Surround module)
    {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event)
    {
        SPacketBlockChange packet = event.getPacket();
        event.addPostEvent(() ->
        {
            if (module.targets.contains(packet.getBlockPosition()))
            {
                if (packet.getBlockState().getBlock() == Blocks.AIR)
                {
                    module.confirmed.remove(packet.getBlockPosition());
                    if (module.shouldInstant(false))
                    {
                        ListenerMotion.start(module);
                    }
                }
                else if (!packet.getBlockState().getMaterial().isReplaceable())
                {
                    module.confirmed.add(packet.getBlockPosition());
                }
            }
        });
    }

}
