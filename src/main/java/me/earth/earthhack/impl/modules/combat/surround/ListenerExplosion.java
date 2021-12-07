package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.BlockPos;

final class ListenerExplosion extends
        ModuleListener<Surround, PacketEvent.Receive<SPacketExplosion>>
{
    public ListenerExplosion(Surround module)
    {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event)
    {
        SPacketExplosion packet = event.getPacket();
        event.addPostEvent(() ->
        {
            for (BlockPos pos : packet.getAffectedBlockPositions())
            {
                module.confirmed.remove(pos);
                if (module.shouldInstant(false))
                {
                    ListenerMotion.start(module);
                }
            }
        });
    }

}
