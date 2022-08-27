package me.earth.earthhack.impl.modules.misc.autocraft;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;

final class ListenerTick
        extends ModuleListener<AutoCraft, TickEvent>
{
    public ListenerTick(AutoCraft module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (module.currentTask != null) {
            System.out.println("current task:" + module.currentTask.getRecipe().getRecipeOutput().getItem().getRegistryName());
        }
        if (module.lastTask != null) {
            System.out.println("last task:" + module.lastTask.getRecipe().getRecipeOutput().getItem().getRegistryName());
        }
        if (module.delayTimer
                .passed(module.delay.getValue())
                && module.currentTask == null)
        {
            module.currentTask = module.dequeue();
        }

        if (module.currentTask != null
                && module.currentTask.isInTable()
                && !(mc.currentScreen instanceof GuiCrafting))
        {
            if (module.getCraftingTable() == null
                    && InventoryUtil.findBlock(Blocks.CRAFTING_TABLE, false) == -1
                    && module.craftTable.getValue())
            {
                module.lastTask = module.currentTask;
                module.currentTask = new AutoCraft.CraftTask("crafting_table", 1);
            }
            module.shouldTable = true;
            return;
        }

        if (module.lastTask != null
                && InventoryUtil.findBlock(Blocks.CRAFTING_TABLE, false) != -1)
        {
            module.currentTask = module.lastTask;
            module.lastTask = null;
        }

        if (module.clickDelay.getValue() != 0)
        {
            if (module.clickDelayTimer
                    .passed(module.clickDelay.getValue())
                    && module.currentTask != null
                    && (!module.currentTask.isInTable() || mc.currentScreen instanceof GuiCrafting))
            {
                module.currentTask.updateSlots();
                int windowId = 0;
                if (module.currentTask.isInTable()) {
                    assert mc.currentScreen != null;
                    windowId = ((GuiContainer) mc.currentScreen).inventorySlots.windowId;
                }
                if (module.currentTask.step < module.currentTask.getSlotToSlotMap().size())
                {
                    AutoCraft.SlotEntry entry = module.currentTask.getSlotToSlotMap().get(module.currentTask.getStep());
                    System.out.println("inventory slot:" + entry.getInventorySlot());
                    System.out.println("gui slot:" + entry.getGuiSlot());
                    mc.playerController.windowClick(windowId, entry.getInventorySlot(), 0, ClickType.PICKUP, mc.player);
                    for (int i = 0; i < module.currentTask.runs; i++) {
                        mc.playerController.windowClick(windowId, entry.getGuiSlot(), 1, ClickType.PICKUP, mc.player);
                    }
                    mc.playerController.windowClick(windowId, entry.getInventorySlot(), 0, ClickType.PICKUP, mc.player);
                    module.currentTask.step++;
                }
                else if (module.currentTask.step == module.currentTask.getSlotToSlotMap().size())
                {
                    mc.playerController.windowClick(windowId, 0, 0, ClickType.QUICK_MOVE, mc.player);
                    module.currentTask = null;
                    module.delayTimer.reset();
                    if (mc.currentScreen instanceof GuiCrafting) mc.displayGuiScreen(null);
                }
            }
        }
    }
}
