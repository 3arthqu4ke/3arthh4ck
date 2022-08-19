package me.earth.earthhack.impl.modules.misc.autoregear;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

// TODO: make code not shit
final class ListenerTick
        extends ModuleListener<AutoRegear, TickEvent>
{
    public ListenerTick(AutoRegear module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (mc.currentScreen instanceof GuiShulkerBox)
        {
            for (int i = 0; i < 36; i++)
            {
                if (module.delayTimer.passed(module.delay.getValue()))
                {
                    Setting<?> setting = module.getSettingFromSlot(i);
                    if (setting == null) continue;
                    int itemId = Integer.parseInt(setting.getName().split(":")[1]);
                    if (itemId == 0) continue;
                    Item item = Item.getItemById(itemId);
                    int shulkerSlot = InventoryUtil.findItem(item, ((GuiShulkerBox) mc.currentScreen).inventorySlots);
                    ItemStack stackInSlot = ((GuiContainer) mc.currentScreen).inventorySlots.getInventory().get(i + 27);
                    if (stackInSlot.getMaxStackSize() == 1
                            || stackInSlot.getMaxStackSize() == stackInSlot.getCount()
                            || stackInSlot.getItem() != item && stackInSlot.getItem() != Items.AIR
                            || shulkerSlot == -1
                            || shulkerSlot > 26) continue;
                    mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, shulkerSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, i + 27, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, shulkerSlot, 0, ClickType.PICKUP, mc.player);
                    module.delayTimer.reset();
                }
            }
        }
        else if (mc.currentScreen instanceof GuiChest
                && module.shouldRegear
                && module.grabShulker.getValue()
                && !module.hasKit())
        {
            int slot = -1;
            // TODO: improve method of finding kits in chests
            for (int i = 0; i < 28; i++)
            {
                boolean foundExp = false;
                boolean foundCrystals = false;
                boolean foundGapples = false;
                ItemStack stack = ((GuiContainer) mc.currentScreen).inventorySlots.getInventory().get(i);
                if (stack.getItem() instanceof ItemShulkerBox)
                {
                    NBTTagCompound tagCompound = stack.getTagCompound();
                    if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10))
                    {
                        NBTTagCompound blockEntityTag =
                                tagCompound.getCompoundTag("BlockEntityTag");

                        if (blockEntityTag.hasKey("Items", 9))
                        {
                            NonNullList<ItemStack> nonNullList =
                                    NonNullList.withSize(27, ItemStack.EMPTY);

                            ItemStackHelper.loadAllItems(blockEntityTag, nonNullList);
                            for (ItemStack stack1 : nonNullList)
                            {
                                if (stack1.getItem() == Items.GOLDEN_APPLE)
                                {
                                    foundGapples = true;
                                }
                                else if (stack1.getItem() == Items.EXPERIENCE_BOTTLE)
                                {
                                    foundExp = true;
                                }
                                else if (stack1.getItem() == Items.END_CRYSTAL)
                                {
                                    foundCrystals = true;
                                }
                            }
                            if (foundExp
                                    && foundGapples
                                    && foundCrystals)
                            {
                                slot = i;
                            }
                            else
                            {
                                if (!module.hasKit()
                                        && module.getShulkerBox() == null)
                                {
                                    module.shouldRegear = false;
                                    mc.player.closeScreen();
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (slot != -1)
            {
                int emptySlot = InventoryUtil.findInInventory(stack -> stack.isEmpty() || stack.getItem() == Items.AIR, false);
                if (emptySlot != -1)
                {
                    mc.playerController.windowClick(((GuiContainer) mc.currentScreen).inventorySlots.windowId, slot, 0, ClickType.QUICK_MOVE, mc.player);
                }
            }
            mc.player.closeScreen();
        }
    }
}
