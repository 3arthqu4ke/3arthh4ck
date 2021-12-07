package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerDestroyEntities extends
        ModuleListener<AutoCrystal, PacketEvent.Receive<SPacketDestroyEntities>>
{
    public ListenerDestroyEntities(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event)
    {
        if (module.destroyThread.getValue())
        {
            module.threadHelper.schedulePacket(event);
        }
    }

}
