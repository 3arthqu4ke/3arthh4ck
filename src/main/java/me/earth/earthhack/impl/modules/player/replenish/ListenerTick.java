package me.earth.earthhack.impl.modules.player.replenish;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

final class ListenerTick extends ModuleListener<Replenish, TickEvent>
{
    private static final ModuleCache<XCarry> XCARRY =
        Caches.getModule(XCarry.class);

    /** Manages resetting of the hotbar. */
    private boolean reset;

    public ListenerTick(Replenish module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (event.isSafe()
                && !mc.player.isCreative()
                && !(mc.currentScreen instanceof GuiContainer
                    && !(mc.currentScreen instanceof GuiInventory
                        && module.inInvWithMiddleClick.getValue()
                        && Mouse.isButtonDown(2))))
        {
            reset = false;
            if (!module.timer.passed(module.delay.getValue())
                    || !module.replenishInLoot.getValue()
                        && !mc.world
                              .getEntitiesWithinAABB(EntityItem.class,
                                    RotationUtil.getRotationPlayer()
                                                .getEntityBoundingBox())
                              .isEmpty())
            {
                return;
            }

            for (int i = 0; i < 9; i++)
            {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                Item iItem = stack.getItem();
                if (module.isStackValid(stack)
                        && stack.getCount() <= module.threshold.getValue()
                        && (stack.getCount() < stack.getMaxStackSize()
                            || stack.isEmpty()))
                {
                    ItemStack before = module.hotbar.get(i);
                    if (before != null
                            && module.isStackValid(stack)
                            && !before.isEmpty()
                            && (before.getItem() == stack.getItem()
                                || stack.isEmpty()))
                    {
                        int slot = findSlot(stack.isEmpty()
                                                ? before
                                                : stack,
                                            stack.getCount());
                        if (slot != -1)
                        {
                            boolean drag = slot == -2;

                            boolean diff = false;
                            if (slot > 46)
                            {
                                slot -= 100;
                                if (slot < 1)
                                {
                                    module.hotbar.set(i, stack.copy());
                                    continue;
                                }

                                diff = true;
                            }

                            int finalI = i + 36;
                            int finalSlot = slot;
                            boolean finalDiff = diff;
                            Item sItem = InventoryUtil.get(slot).getItem();
                            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                            {
                                if (InventoryUtil.get(finalI)
                                                 .getItem() != iItem
                                    || InventoryUtil.get(finalSlot)
                                                    .getItem() != sItem)
                                {
                                    return;
                                }

                                Managers.NCP.startMultiClick();

                                if (!drag)
                                {
                                    InventoryUtil.click(finalSlot);
                                }

                                InventoryUtil.click(finalI);

                                if (finalDiff && module.putBack.getValue())
                                {
                                    InventoryUtil.click(finalSlot);
                                }

                                Managers.NCP.releaseMultiClick();
                            });

                            module.timer.reset();
                            if (module.delay.getValue() != 0)
                            {
                                break;
                            }

                            continue;
                        }
                    }
                }

                module.hotbar.set(i, stack.copy());
            }
        }
        else if (!reset)
        {
            // If we are in a GuiContainer we don't want the
            // hotbar to be rearranged after we close it etc..
            module.clear();
            reset = true;
        }
    }

    /**
     * Finds the best ItemStack based on the given one.
     * Tries to find the stack that fills the given one best,
     * if such a stack can't be found (size isn't bigger than minSize),
     * the biggest fitting stack in the Inventory will be returned.
     *
     * @param current the stack to fill.
     * @param count the count of the given stack.
     *
     * @return <p> <tt>-1 ==</tt> No Slot found.
     *         <p> <tt>-2 ==</tt> DragSlot
     *         <p> <tt>slot > 46</tt> means the <tt>slot - 100</tt>
     *         and the stack size will fill the given one over the limit.
     *         <p> otherwise just the inventory slot.
     */
    private int findSlot(ItemStack current, int count)
    {
        int result = -1;

        int maxDiff  = current.getMaxStackSize() - count;
        int minSize  = current.getMaxStackSize() > module.minSize.getValue()
                            ? module.minSize.getValue()
                            : current.getMaxStackSize();

        int maxSize   = 0;
        int maxIndex  = -1;
        int limitSize = 0;

        if (InventoryUtil.canStack(current, mc.player.inventory.getItemStack()))
        {
            return -2;
        }

        boolean xCarry = XCARRY.isEnabled();
        for (int i = 9; i <= 36; i++)
        {
            // this way we dont prioritize XCarry.
            if (i == 5)
            {
                break;
            }

            if (i == 36)
            {
                if (xCarry)
                {
                    i = 1;
                }
                else
                {
                    break;
                }
            }

            ItemStack stack = mc.player.inventoryContainer
                                       .getInventory()
                                       .get(i);

            if (InventoryUtil.canStack(current, stack))
            {
                if (stack.getCount() > maxDiff)
                {
                    if (stack.getCount() > maxSize)
                    {
                        maxIndex = i;
                        maxSize  = stack.getCount();
                    }
                }
                else if (stack.getCount() > limitSize)
                {
                    result    = i;
                    limitSize = stack.getCount();
                }
            }
        }

        if (maxIndex != -1
                && (result == -1
                    || mc.player.inventoryContainer
                                .getInventory()
                                .get(result)
                                .getCount() < minSize))
        {
            return maxIndex + 100;
        }

        return result;
    }

}
