package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class ListenerSwitch
    extends ModuleListener<Management, PacketEvent.Send<CPacketHeldItemChange>> {
    public ListenerSwitch(Management module) {
        super(module,
              PacketEvent.Send.class,
              Integer.MAX_VALUE - 100,
              CPacketHeldItemChange.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketHeldItemChange> event) {
        CooldownBypass bypass = module.manualCooldownBypass.getValue();
        if (bypass != CooldownBypass.None) {
            bypass.switchTo(event.getPacket().getSlotId());
            event.setCancelled(true);
        }
    }

}
