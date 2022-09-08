package me.earth.earthhack.impl.modules.player.sorter;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.player.cleaner.Cleaner;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

final class ListenerMotion extends ModuleListener<Sorter, MotionUpdateEvent>
{
    private static final ModuleCache<AutoArmor> AUTO_ARMOR =
            Caches.getModule(AutoArmor.class);
    private static final ModuleCache<Cleaner> CLEANER =
            Caches.getModule(Cleaner.class);

    private final Set<Item> missing = new HashSet<>();

    public ListenerMotion(Sorter module)
    {
        super(module, MotionUpdateEvent.class, 999999);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (!module.timer.passed(module.delay.getValue())
            || !module.sort.getValue()
            || event.getStage() != Stage.PRE
            || !Managers.NCP.getClickTimer()
                        .passed(module.globalDelay.getValue())
            || mc.player.isCreative()
            || !InventoryUtil.validScreen()
            || AUTO_ARMOR.returnIfPresent(AutoArmor::isActive, false)
            || !module.sortInInv.getValue()
                && mc.currentScreen instanceof GuiInventory
            || !module.sortInLoot.getValue()
                && !mc.world
                    .getEntitiesWithinAABB(EntityItem.class,
                        RotationUtil.getRotationPlayer()
                                .getEntityBoundingBox())
                    .isEmpty()
            || CLEANER.isEnabled()
                && !CLEANER.get().getTimer().passed(
                        CLEANER.get().getDelay() * 3L))
        {
            return;
        }

        InventoryLayout layout = module.current;
        if (layout == null)
        {
            return;
        }

        Item fallbackItem = null;
        Item otherFallbackItem = null;
        int fallback = -1;
        int otherFallback = -1;
        boolean emptyFallback = false;
        missing.clear();
        for (int i = 44; i > 8; i--)
        {
            ItemStack s = InventoryUtil.get(i);
            Item shouldBeHere = layout.getItem(i);
            if (shouldBeHere == s.getItem()
                || shouldBeHere == Items.AIR
                || missing.contains(shouldBeHere))
            {
                continue;
            }

            if (mc.player.inventory.getItemStack().getItem() == shouldBeHere)
            {
                int finalSlot = i;
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                    InventoryUtil.click(finalSlot));

                return;
            }

            int slot = getSlot(shouldBeHere, s.getItem(), i, layout);
            if (slot == -2)
            {
                return;
            }
            else if (slot != -1
                    && (fallback == -1 || !emptyFallback && s.isEmpty()))
            {
                fallback = slot;
                otherFallback = i;
                emptyFallback = s.isEmpty();
                fallbackItem = InventoryUtil.get(slot).getItem();
                otherFallbackItem = s.getItem();
            }
        }

        if (fallback != -1)
        {
            click(fallback, otherFallback, fallbackItem, otherFallbackItem);
        }
    }

    private int getSlot(Item shouldBeHere,
                        Item inSlot,
                        int slot,
                        InventoryLayout layout)
    {
        int result = -1;
        int hotbarSlot = -1;
        for (int i = 44; i > 8; i--)
        {
            if (i == slot)
            {
                continue;
            }

            Item item = InventoryUtil.get(i).getItem();
            boolean hotbar;
            if ((hotbar = item == layout.getItem(i))
                && !(module.ensureHotbar.getValue() && slot >= 36 && i < 36))
            {
                continue;
            }

            if (item == shouldBeHere)
            {
                if (hotbar)
                {
                    hotbarSlot = i;
                }
                else
                {
                    result = i;
                }

                Item shouldBeThere = layout.getItem(i);
                if (shouldBeThere == inSlot)
                {
                    click(i, slot, item, inSlot);
                    return -2;
                }
            }
        }

        if (result == -1)
        {
            if (hotbarSlot != -1)
            {
                return hotbarSlot;
            }

            missing.add(shouldBeHere);
        }

        return result;
    }

    private void click(int from, int to, Item inSlot, Item inToSlot)
    {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
        {
            Item slotItem = InventoryUtil.get(from).getItem();
            Item toItem   = InventoryUtil.get(to).getItem();
            if (slotItem == inSlot && inToSlot == toItem)
            {
                InventoryUtil.click(from);
                InventoryUtil.click(to);
                InventoryUtil.click(from);
                module.timer.reset();
            }
        });
    }

}
