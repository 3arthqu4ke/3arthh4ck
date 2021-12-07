package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;

final class ListenerSetSlot extends
        ModuleListener<Packets, PacketEvent.Receive<SPacketSetSlot>>
{
    public ListenerSetSlot(Packets module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                SPacketSetSlot.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSetSlot> event)
    {
        if (!module.fastSetSlot.getValue() || event.isCancelled())
        {
            return;
        }

        EntityPlayer player = mc.player;
        if (player == null)
        {
            return;
        }

        int slot        = event.getPacket().getSlot();
        int id          = event.getPacket().getWindowId();
        ItemStack stack = event.getPacket().getStack();

        if (id == -1)
        {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                player.inventory.setItemStack(stack));
        }
        else if (id == -2)
        {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                player.inventory.setInventorySlotContents(slot, stack));
        }
        else
        {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
            {
                boolean badTab = false;
                GuiScreen current = mc.currentScreen;
                if (current instanceof GuiContainerCreative)
                {
                    GuiContainerCreative creative =
                            (GuiContainerCreative) current;
                    badTab = creative.getSelectedTabIndex()
                            != CreativeTabs.INVENTORY.getIndex();
                }

                if (id == 0 && slot >= 36 && slot < 45)
                {
                    if (!stack.isEmpty())
                    {
                        ItemStack inSlot = InventoryUtil.get(slot);
                        if (inSlot.isEmpty()
                                || inSlot.getCount() < stack.getCount())
                        {
                            stack.setAnimationsToGo(5);
                        }
                    }

                    player.inventoryContainer.putStackInSlot(slot, stack);
                    return;
                }

                Container container = player.openContainer;
                if (id == container.windowId && (id != 0 || !badTab))
                {
                    container.putStackInSlot(slot, stack);
                }
            });
        }
    }

}
