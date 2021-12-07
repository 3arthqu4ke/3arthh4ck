package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketExplosion;

final class ListenerExplosion extends
        ModuleListener<AutoCrystal, PacketEvent.Receive<SPacketExplosion>>
{
    public ListenerExplosion(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event)
    {
        if (module.explosionThread.getValue()
                && !event.getPacket().getAffectedBlockPositions().isEmpty())
        {
            module.threadHelper.schedulePacket(event);
        }
    }

}
