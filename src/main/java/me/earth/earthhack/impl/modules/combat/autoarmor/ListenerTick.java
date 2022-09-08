package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.DamageStack;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.SingleMendingSlot;
import me.earth.earthhack.impl.modules.player.noinventorydesync.MendingStage;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class ListenerTick extends ModuleListener<AutoArmor, TickEvent>
{
    private static final ModuleCache<XCarry> XCARRY =
        Caches.getModule(XCarry.class);

    public ListenerTick(AutoArmor module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (!event.isSafe() || checkDesync())
        {
            module.stage = MendingStage.MENDING;
            module.putBackClick = null;
            return;
        }

        module.stackSet = false;
        module.queuedSlots.clear();
        module.windowClicks.clear();
        module.desyncMap.entrySet().removeIf(
            e -> System.currentTimeMillis() - e.getValue().getTimeStamp()
                > module.removeTime.getValue());

        if (module.softInInv.getValue()
            && mc.currentScreen instanceof GuiInventory) {
            return;
        }

        if (InventoryUtil.validScreen())
        {
            if (module.canAutoMend())
            {
                module.queuedSlots.add(-2); // blacklist drag slot
                ItemStack setStack = module.setStack();
                boolean setStackIsNull = setStack == null;
                boolean singleMend = module.singleMend.getValue();
                if (setStack == null)
                {
                    if (!singleMend)
                    {
                        return;
                    }

                    setStack = mc.player.inventory.getItemStack();
                    module.queuedSlots.remove(-2);
                }

                int mendBlock = module.mendBlock.getValue();
                if (mendBlock > 0 && module.stage != MendingStage.MENDING)
                {
                    if (module.dontBlockWhenFull.getValue() && (setStackIsNull || isFull()))
                    {
                        module.stage = MendingStage.MENDING;
                        return;
                    }

                    if (module.stage == MendingStage.BLOCK)
                    {
                        if (module.mendingTimer.passed(mendBlock))
                        {
                            module.stage = MendingStage.TAKEOFF;
                            module.mendingTimer.reset();
                        }
                        else
                        {
                            return;
                        }
                    }

                    if (module.stage == MendingStage.TAKEOFF && module.mendingTimer.passed(module.postBlock.getValue()))
                    {
                        module.stage = MendingStage.MENDING;
                    }
                }

                if (singleMend)
                {
                    doSingleMend(setStack, mendBlock);
                }
                else
                {
                    doNormalMend(setStack, mendBlock);
                }
            }
            else
            {
                module.stage = MendingStage.MENDING;
                module.unblockMendingSlots();
                Map<EntityEquipmentSlot, Integer> map =
                    module.mode
                        .getValue()
                        .setup(XCARRY.isEnabled(),
                               !module.curse.getValue(),
                               module.prioLow.getValue(),
                               module.prioThreshold.getValue());
                int last = -1;
                ItemStack drag = mc.player.inventory.getItemStack();
                for (Map.Entry<EntityEquipmentSlot, Integer> entry : map.entrySet())
                {
                    if (entry.getValue() == 8)
                    {
                        int slot = AutoArmor.fromEquipment(entry.getKey());
                        if (slot != -1 && slot != 45)
                        {
                            ItemStack inSlot = InventoryUtil.get(slot);
                            module.queueClick(slot, inSlot, drag);
                            drag = inSlot;
                            last = slot;
                        }

                        map.remove(entry.getKey()); // ok, since we break
                        break;
                    }
                }

                for (Map.Entry<EntityEquipmentSlot, Integer> entry : map.entrySet())
                {
                    int slot = AutoArmor.fromEquipment(entry.getKey());
                    if (slot != -1 && slot != 45 && entry.getValue() != null)
                    {
                        int i = entry.getValue();
                        ItemStack inSlot = InventoryUtil.get(i);
                        module.queueClick(i, inSlot, drag)
                              .setDoubleClick(module.doubleClicks.getValue());

                        if (!drag.isEmpty())
                        {
                            module.queuedSlots.add(i);
                        }

                        drag = inSlot;
                        inSlot = InventoryUtil.get(slot);
                        module.queueClick(slot, inSlot, drag);
                        drag = inSlot;
                        last = slot;
                    }
                }

                if (module.putBack.getValue())
                {
                    if (last != -1)
                    {
                        ItemStack stack = InventoryUtil.get(last);
                        if (!stack.isEmpty())
                        {
                            module.queuedSlots.add(-2);
                            int air = AutoArmor.findItem(Items.AIR,
                                                         XCARRY.isEnabled(),
                                                         module.queuedSlots);
                            if (air != -1)
                            {
                                ItemStack inSlot = InventoryUtil.get(air);
                                module.putBackClick =
                                    module.queueClick(air, inSlot, drag);

                                module.putBackClick.addPost(() ->
                                                                module.putBackClick = null);
                            }
                        }
                    }
                    else if (module.putBackClick != null
                        && module.putBackClick.isValid())
                    {
                        module.queueClick(module.putBackClick);
                    }
                    else
                    {
                        module.putBackClick = null;
                    }
                }
            }
        }
        else
        {
            module.stage = MendingStage.MENDING;
        }

        module.runClick();
    }

    private boolean checkDesync()
    {
        if (module.noDesync.getValue()
            && InventoryUtil.validScreen()
            && module.timer.passed(module.checkDelay.getValue())
            && module.desyncTimer.passed(module.desyncDelay.getValue())
            && module.propertyTimer.passed(module.propertyDelay.getValue()))
        {
            int bestSlot = -1;
            int clientValue = 0;
            boolean foundType = false;
            int armorValue  = mc.player.getTotalArmorValue();
            for (int i = 5; i < 9; i++)
            {
                ItemStack stack = mc.player.inventoryContainer
                    .getSlot(i)
                    .getStack();
                if (stack.isEmpty() && !foundType)
                {
                    bestSlot = i;
                    if (module.lastType == AutoArmor.fromSlot(i))
                    {
                        foundType = true;
                    }
                }
                else if (stack.getItem() instanceof ItemArmor)
                {
                    ItemArmor itemArmor = (ItemArmor) stack.getItem();
                    clientValue += itemArmor.damageReduceAmount;
                }
            }

            if (clientValue != armorValue
                && module.timer.passed(module.delay.getValue()))
            {
                if (module.illegalSync.getValue())
                {
                    ModuleUtil.sendMessage(module, TextColor.RED + "Desync!");
                    InventoryUtil.illegalSync();
                }
                else if (bestSlot != -1
                    && AutoArmor.getSlot(mc.player.inventory.getItemStack())
                    == AutoArmor.fromSlot(bestSlot))
                {
                    ModuleUtil.sendMessage(module, TextColor.RED
                        + "Desync! (Code: " + bestSlot + ")");

                    Item i = InventoryUtil.get(bestSlot).getItem();
                    InventoryUtil.clickLocked(bestSlot, bestSlot, i, i);
                }
                else
                {
                    ModuleUtil.sendMessage(module, TextColor.RED + "Desync!");
                    Item i = InventoryUtil.get(20).getItem();
                    InventoryUtil.clickLocked(20, 20, i, i);
                }

                module.resetTimer();
                module.desyncTimer.reset();
                return true;
            }
        }

        return false;
    }

    private boolean isFull()
    {
        boolean added = false;
        if (!module.dragTakeOff.getValue())
        {
            added = module.queuedSlots.add(-2);
        }

        boolean result = AutoArmor.findItem(Items.AIR, XCARRY.isEnabled(), module.queuedSlots) == -1;
        if (added)
        {
            module.queuedSlots.remove(-2);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void doNormalMend(ItemStack dragIn, int mendBlock)
    {
        List<DamageStack> stacks = new ArrayList<>(4);
        for (int i = 5; i < 9; i++)
        {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) != 0)
            {
                float percent = DamageUtil.getPercent(stack);
                if (percent > ((Setting<Integer>) module.damages[i - 5]).getValue())
                {
                    stacks.add(new DamageStack(stack, percent, i));
                }
            }
        }

        // Sort so we take the ones with the most percent off first.
        stacks.sort(DamageStack::compareTo);
        MutableWrapper<ItemStack> drag = new MutableWrapper<>(dragIn);
        for (DamageStack stack : stacks)
        {
            if (checkDamageStack(stack, mendBlock, drag))
            {
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doSingleMend(ItemStack dragIn, int mendBlock)
    {
        boolean allBlocked = true;
        for (SingleMendingSlot singleMendingSlot : module.singleMendingSlots)
        {
            allBlocked = allBlocked && singleMendingSlot.isBlocked();
        }

        if (allBlocked)
        {
            module.unblockMendingSlots();
        }

        List<DamageStack> stacks = new ArrayList<>(4);
        for (int i = 5; i < 9; i++)
        {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) != 0)
            {
                float percent = DamageUtil.getPercent(stack);
                stacks.add(new DamageStack(stack, percent, i));
            }
        }

        stacks.sort(DamageStack::compareTo);
        if (stacks.size() <= 0)
        {
            int bestSlot = -1;
            MutableWrapper<Float> lowest = new MutableWrapper<>(Float.MAX_VALUE);
            MutableWrapper<ItemStack> bestStack = new MutableWrapper<>(ItemStack.EMPTY);
            for (SingleMendingSlot singleMendingSlot : module.singleMendingSlots)
            {
                if (!singleMendingSlot.isBlocked())
                {
                    int slot = AutoArmor.iterateItems(XCARRY.isEnabled(), module.queuedSlots, stack ->
                    {
                        if (AutoArmor.getSlot(stack) == singleMendingSlot.getSlot())
                        {
                            float percent = DamageUtil.getPercent(stack);
                            if (percent < lowest.get())
                            {
                                bestStack.set(stack);
                                lowest.set(percent);
                                return true;
                            }
                        }

                        return false;
                    });

                    bestSlot = slot == -1 ? bestSlot : slot;
                }
            }

            if (bestSlot != -1 && lowest.get() < 100.0f)
            {
                EntityEquipmentSlot equipmentSlot = AutoArmor.getSlot(bestStack.get());
                if (equipmentSlot != null)
                {
                    int slot = AutoArmor.fromEquipment(equipmentSlot);
                    if (bestSlot != -2)
                    {
                        module.queueClick(bestSlot, bestStack.get(), dragIn, slot)
                              .setDoubleClick(module.doubleClicks.getValue());
                    }

                    module.queueClick(slot, InventoryUtil.get(slot), bestStack.get());
                }
            }
            else if (!allBlocked)
            {
                module.unblockMendingSlots();
            }
        }
        else if (stacks.size() == 1)
        {
            DamageStack stack = stacks.get(0);
            SingleMendingSlot mendingSlot = Arrays.stream(module.singleMendingSlots)
                                                  .filter(s -> s.getSlot() == AutoArmor.getSlot(stack.getStack()))
                                                  .findFirst()
                                                  .orElse(null);
            if (mendingSlot != null
                && stack.getDamage() > ((Setting<Integer>) module.damages[stack.getSlot() - 5]).getValue())
            {
                MutableWrapper<ItemStack> drag = new MutableWrapper<>(dragIn);
                checkDamageStack(stack, mendBlock, drag);
                mendingSlot.setBlocked(true);
            }
        }
        else
        {
            MutableWrapper<ItemStack> drag = new MutableWrapper<>(dragIn);
            for (DamageStack stack : stacks)
            {
                if (checkDamageStack(stack, mendBlock, drag))
                {
                    return;
                }
            }

            module.stage = MendingStage.MENDING;
            // TODO: "hotswap"
        }
    }

    private boolean checkMendingStage(int mendBlock)
    {
        if (mendBlock > 0 && module.stage == MendingStage.MENDING)
        {
            module.stage = MendingStage.BLOCK;
            module.mendingTimer.reset();
            return true;
        }

        return false;
    }

    private boolean checkDamageStack(DamageStack stack, int mendBlock, MutableWrapper<ItemStack> drag)
    {
        ItemStack sStack = stack.getStack();
        int slot = AutoArmor.findItem(Items.AIR, XCARRY.isEnabled(), module.queuedSlots);
        if (slot == -1)
        {
            if (module.dragTakeOff.getValue()
                && (module.stackSet
                || mc.player.inventory.getItemStack()
                                      .isEmpty()))
            {
                if (checkMendingStage(mendBlock))
                {
                    return true;
                }

                module.queueClick(stack.getSlot(), sStack, drag.get(), -1);
            }

            return true;
        }
        else if (slot != -2 && mc.player.inventory.getItemStack().isEmpty())
        {
            if (checkMendingStage(mendBlock))
            {
                return true;
            }

            module.queueClick(stack.getSlot(), sStack, drag.get(), slot)
                  .setDoubleClick(module.doubleClicks.getValue());

            drag.set(sStack);
            ItemStack inSlot = InventoryUtil.get(slot);
            module.queueClick(slot, inSlot, drag.get());
            module.queuedSlots.add(slot);
            drag.set(inSlot);
        }

        return false;
    }

}