package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.DesyncClick;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;

final class ListenerSetSlot extends
        ModuleListener<AutoArmor, PacketEvent.Receive<SPacketSetSlot>>
{
    public ListenerSetSlot(AutoArmor module)
    {
        super(module, PacketEvent.Receive.class, SPacketSetSlot.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSetSlot> event)
    {
        SPacketSetSlot packet = event.getPacket();
        if (!module.noDuraDesync.getValue()
            || event.isCancelled()
            || packet.getWindowId() != 0
            || packet.getSlot() < 5
            || packet.getSlot() > 8)
        {
            return;
        }

        event.setCancelled(true);
        mc.addScheduledTask(() ->
        {
            if (mc.player == null)
            {
                return;
            }

            DesyncClick click = module.desyncMap.get(packet.getSlot());
            if (click == null
                    || System.currentTimeMillis() - click.getTimeStamp()
                                > module.removeTime.getValue())
            {
                packet.processPacket(mc.player.connection);
            }
            else
            {
                ItemStack stack = InventoryUtil.get(packet.getSlot());
                if (InventoryUtil.equals(stack, packet.getStack()))
                {
                    packet.processPacket(mc.player.connection);
                    return;
                }

                ItemStack drag = mc.player.inventory.getItemStack();
                if (InventoryUtil.equals(drag, packet.getStack()))
                {
                    mc.player.inventory.setItemStack(packet.getStack());
                    return;
                }

                int slot = click.getClick().getTarget();
                if (slot > 0 && slot < 45)
                {
                    stack = InventoryUtil.get(slot);
                    if (InventoryUtil.equals(stack, packet.getStack()))
                    {
                        InventoryUtil.put(slot, packet.getStack());
                    }
                }
            }
        });
    }

}