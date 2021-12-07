package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketEntityAction;

public class ListenerSneak
        extends ModuleListener<Phase, PacketEvent.Send<CPacketEntityAction>>
{
    public ListenerSneak(Phase module)
    {
        super(module, PacketEvent.Send.class, CPacketEntityAction.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketEntityAction> event)
    {
        if (event.getPacket().getAction() == CPacketEntityAction.Action.START_SNEAKING
                && module.isPhasing()
                && module.cancelSneak.getValue()
                && mc.gameSettings.keyBindSneak.isKeyDown())
        {
            event.setCancelled(true);
        }
    }
}
