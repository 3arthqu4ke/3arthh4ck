package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketHeldItemChange;

final class ListenerHeldItemChange extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketHeldItemChange>>
{
    public ListenerHeldItemChange(Packets module)
    {
        super(module, PacketEvent.Receive.class, SPacketHeldItemChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketHeldItemChange> event)
    {
        if (module.noHandChange.getValue() && mc.player != null)
        {
            event.setCancelled(true);
            mc.addScheduledTask(Locks.wrap(Locks.PLACE_SWITCH_LOCK, () ->
            {
                if (mc.player == null || mc.getConnection() == null)
                {
                    return;
                }

                int l = mc.player.inventory.currentItem;
                if (l != event.getPacket().getHeldItemHotbarIndex())
                {
                    mc.player.inventory.currentItem = l;
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(l));
                }
            }));
        }
    }

}
