package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockState extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketBlockChange>>
{
    public ListenerBlockState(Packets module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event)
    {
        if (module.fastBlockStates.getValue())
        {
            SPacketBlockChange p = event.getPacket();
            module.stateMap.put(p.getBlockPosition(), p.getBlockState());
            mc.addScheduledTask(() ->
            {
                module.stateMap.remove(p.getBlockPosition());
            });
        }
    }

}
