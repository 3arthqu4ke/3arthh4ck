package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.play.server.SPacketSetSlot;

final class ListenerSetSlot extends
        ModuleListener<Offhand, PacketEvent.Receive<SPacketSetSlot>>
{
    public ListenerSetSlot(Offhand module)
    {
        super(module, PacketEvent.Receive.class, SPacketSetSlot.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSetSlot> event)
    {
        module.setSlotTimer.reset();
        if (!module.async.getValue()
            || module.asyncTimer.passed(module.asyncCheck.getValue())
            || module.asyncSlot == -1
            || event.getPacket().getSlot() != module.asyncSlot)
        {
            return;
        }

        event.setCancelled(true);
        if (PingBypass.isConnected()
            && module.fixPingBypassAsyncSlot.getValue()) {
            event.setPingBypassCancelled(true);
        }

        module.asyncSlot = -1;
    }

}
