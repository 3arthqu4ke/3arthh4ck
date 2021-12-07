package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemShield;
import net.minecraft.network.play.server.SPacketEntityEquipment;

final class ListenerEntityEquipment extends
        ModuleListener<KillAura, PacketEvent.Receive<SPacketEntityEquipment>>
{
    public ListenerEntityEquipment(KillAura module)
    {
        super(module, PacketEvent.Receive.class, SPacketEntityEquipment.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityEquipment> event)
    {
        SPacketEntityEquipment packet = event.getPacket();
        if (packet.getEquipmentSlot().getIndex() == 1
             && module.cancelEntityEquip.getValue()
             && packet.getItemStack().getItem() instanceof ItemAir
             && mc.player.getHeldItemOffhand().getItem() instanceof ItemShield)
        {
            event.setCancelled(true);
        }
    }

}
