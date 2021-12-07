package me.earth.earthhack.impl.modules.player.nohunger;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketEntityAction;

final class ListenerEntityAction extends
        ModuleListener<NoHunger, PacketEvent.Send<CPacketEntityAction>>
{
    public ListenerEntityAction(NoHunger module)
    {
        super(module, PacketEvent.Send.class, CPacketEntityAction.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketEntityAction> event)
    {
        if (module.sprint.getValue())
        {
            CPacketEntityAction p = event.getPacket();
            if (p.getAction() == CPacketEntityAction.Action.START_SPRINTING
                    || p.getAction() ==
                                CPacketEntityAction.Action.STOP_SPRINTING)
            {
                event.setCancelled(true);
            }
        }
    }

}
