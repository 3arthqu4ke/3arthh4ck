package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.DamageStack;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
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
    @SuppressWarnings("unchecked")
    public void invoke(TickEvent event)
    {
        if (!event.isSafe() || checkDesync())
        {
            module.putBackClick = null;
            return;
        }

        module.stackSet = false;
        module.queuedSlots.clear();
        module.windowClicks.clear();
        module.desyncMap.entrySet().removeIf(e ->
            System.currentTimeMillis() - e.getValue().getTimeStamp()
                    > module.removeTime.getValue());
        if (InventoryUtil.validScreen())
        {
            if (module.canAutoMend())
            {
                module.queuedSlots.add(-2); // blacklist drag slot
                List<DamageStack> stacks = new ArrayList<>(4);
                for (int i = 5; i < 9; i++)
                {
                    ItemStack stack = mc.player.inventoryContainer
                                               .getSlot(i)
                                               .getStack();
                    if (!stack.isEmpty())
                    {
                        float percent = DamageUtil.getPercent(stack);
                        if (percent > ((Setting<Integer>) module.damages[i - 5])
                                                                .getValue())
                        {
                            stacks.add(new DamageStack(stack, percent, i));
                        }
                    }
                }

                // Sort so we take the ones with the most percent off first.
                stacks.sort(DamageStack::compareTo);
                ItemStack setStack = module.setStack();
                if (setStack == null)
                {
                    return;
                }

                ItemStack drag = setStack;
                for (DamageStack stack : stacks)
                {
                    ItemStack sStack = stack.getStack();
                    int slot = AutoArmor.findItem(Items.AIR,
                                                  XCARRY.isEnabled(),
                                                  module.queuedSlots);
                    if (slot == -1)
                    {
                        if (module.dragTakeOff.getValue()
                            && (module.stackSet
                                || mc.player.inventory.getItemStack()
                                                      .isEmpty()))
                        {
                            module.queueClick(
                                    stack.getSlot(), sStack, drag, -1);
                        }

                        return;
                    }
                    else if (slot != -2) // slot == -2 shouldn't happen.
                    {
                        module.queueClick(stack.getSlot(), sStack, drag, slot)
                              .setDoubleClick(module.doubleClicks.getValue());

                        drag = sStack;
                        ItemStack inSlot = InventoryUtil.get(slot);
                        module.queueClick(slot, inSlot, drag);
                        module.queuedSlots.add(slot);
                        drag = inSlot;
                    }
                }
            }
            else
            {
                Map<EntityEquipmentSlot, Integer> map =
                    module.mode
                          .getValue()
                          .setup(XCARRY.isEnabled(),
                                 !module.curse.getValue(),
                                 module.prioLow.getValue(),
                                 module.prioThreshold.getValue());
                int last = -1;
                ItemStack drag = mc.player.inventory.getItemStack();
                for (Map.Entry<EntityEquipmentSlot, Integer> entry :
                        map.entrySet())
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

                for (Map.Entry<EntityEquipmentSlot, Integer> entry :
                        map.entrySet())
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

}
