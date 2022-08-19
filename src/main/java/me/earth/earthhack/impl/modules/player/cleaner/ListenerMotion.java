package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.WindowClick;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.*;

final class ListenerMotion extends ModuleListener<Cleaner, MotionUpdateEvent>
{
    private static final ModuleCache<AutoArmor> AUTO_ARMOR =
            Caches.getModule(AutoArmor.class);

    public ListenerMotion(Cleaner module)
    {
        super(module, MotionUpdateEvent.class, 1000000);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (module.action == null
                && !module.timer.passed(module.delay.getValue())
            || !Managers.NCP
                        .getClickTimer()
                        .passed(module.globalDelay.getValue())
            || mc.player.isCreative()
            || !InventoryUtil.validScreen()
            || AUTO_ARMOR.returnIfPresent(AutoArmor::isActive, false)
            || !module.inInventory.getValue()
                    && mc.currentScreen instanceof GuiInventory
            || !module.cleanInLoot.getValue()
                && !mc.world
                      .getEntitiesWithinAABB(EntityItem.class,
                              RotationUtil.getRotationPlayer()
                                          .getEntityBoundingBox())
                      .isEmpty()
                && (!module.cleanWithFull.getValue() || !isInvFull()))
        {
            return;
        }

        if (event.getStage() == Stage.PRE)
        {
            // TODO: use invalidate thingy to only calc when necessary?
            if (module.stack.getValue() && stack()
                    || module.xCarry.getValue() && doXCarry())
            {
                return;
            }

            Map<Item, ItemToDrop> items = new HashMap<>();
            boolean prio = module.prioHotbar.getValue();

            Item drag = null;
            ItemStack draggedStack = mc.player.inventory.getItemStack();
            if (check(draggedStack, -2, items))
            {
                drag = draggedStack.getItem();
            }
            else if (!draggedStack.isEmpty())
            {
                return;
            }

            for (int i = prio ? 44 : 9; prio ? i>8: i<=44;
                //noinspection ConstantConditions (??? am i stupid ???)
                 i = (prio?--i:++i))
            {
                ItemStack stack = InventoryUtil.get(i);
                check(stack, i, items);
            }

            WindowClick action = null;
            if (drag != null)
            {
                ItemToDrop dragged = items.get(drag);
                if (dragged != null && dragged.shouldDrop())
                {
                    action = new WindowClick(-999,
                            ItemStack.EMPTY,
                            mc.player.inventory.getItemStack());
                }
            }
            else
            {
                for (ItemToDrop toDrop : items.values())
                {
                    if (toDrop.shouldDrop())
                    {
                        int s = toDrop.getSlot();
                        action = new WindowClick(
                            -1, ItemStack.EMPTY, s, InventoryUtil.get(s), -1,
                            p -> p.windowClick(
                                    0, s, 1, ClickType.THROW, mc.player));
                        break;
                    }
                }
            }

            if (action != null)
            {
                if (module.rotate.getValue())
                {
                    if (MovementUtil.isMoving())
                    {
                        // behind us
                        event.setYaw(event.getYaw() - 180.0f);
                    }
                    else
                    {
                        event.setYaw(getYaw(event.getYaw()));
                    }

                    // slightly up so we throw further
                    event.setPitch(-5.0f);
                    module.action = action;
                }
                else
                {
                    module.action = action;
                    module.runAction();
                }
            }
        }
        else
        {
            module.runAction();
        }
    }

    private boolean stack()
    {
        ItemStack drag = mc.player.inventory.getItemStack();
        if (drag.isEmpty())
        {
            Map<Item, SettingMap> pref = new HashMap<>();
            Map<Item, Map<Integer, Integer>> corresponding = new HashMap<>();

            for (int i = 44; i > 8; i--)
            {
                ItemStack stack = InventoryUtil.get(i);
                if (stack.isEmpty())
                {
                    continue;
                }

                Item item = stack.getItem();
                Map<Integer, Integer> corr = corresponding.get(item);
                if (corr != null)
                {
                    if (stack.getCount() >= stack.getMaxStackSize()
                        || corr.containsKey(stack.getCount()))
                    {
                        continue;
                    }

                    corr.put(i, stack.getCount());
                }
                else
                {
                    SettingMap map = pref.get(item);
                    if (map == null)
                    {
                        Setting<Integer> setting = getSetting(stack);
                        if (setting == null)
                        {
                            corr = new HashMap<>();
                            if (stack.getCount() != stack.getMaxStackSize())
                            {
                                corr.put(i, stack.getCount());
                            }

                            corresponding.put(item, corr);
                            continue;
                        }

                        map = new SettingMap(setting, new HashMap<>());
                        pref.put(stack.getItem(), map);
                    }

                    map.getMap().put(i, stack.getCount());
                }
            }

            Map<Integer, Map.Entry<Integer, Integer>> best = new TreeMap<>();
            for (Map.Entry<Item, SettingMap> entry : pref.entrySet())
            {
                SettingMap map = entry.getValue();
                if (map.getMap().size() < 2 || map.getSetting().getValue() == 0)
                {
                    continue;
                }

                ItemStack deprec = new ItemStack(entry.getKey());
                int max = map.getSetting().getValue()*deprec.getMaxStackSize();

                int s = 0;
                int total = 0;
                int fullStacks = 0;
                for (int stackCount : map.getMap().values())
                {
                    if (stackCount == deprec.getMaxStackSize())
                    {
                        fullStacks++;
                    }

                    total += stackCount;
                    s++;
                }

                boolean smart = module.smartStack.getValue();
                if (total > max && !smart
                    || fullStacks >= map.getSetting().getValue())
                {
                    continue;
                }

                int m = map.getSetting().getValue();

                Map<Integer, Integer> sMap =
                        CollectionUtil.sortByValue(map.getMap());

                if (findBest(sMap, entry.getKey(), best, smart, s, m))
                {
                    return true;
                }
            }

            Map.Entry<Integer, Integer> b = best.values().stream().findFirst()
                                                                  .orElse(null);
            if (b != null)
            {
                click(b.getValue(), b.getKey());
                return true;
            }

            for (Map.Entry<Item, Map<Integer, Integer>> entry :
                    corresponding.entrySet())
            {
                Map<Integer, Integer> map = entry.getValue();
                if (map.size() < 2)
                {
                    continue;
                }

                Map<Integer, Integer> sort = CollectionUtil.sortByValue(map);
                if (findBest(sort, entry.getKey(), best, false, 0, 0))
                {
                    return true;
                }
            }

            b = best.values().stream().findFirst().orElse(null);
            if (b != null)
            {
                click(b.getValue(), b.getKey());
                return true;
            }
        }
        else if (module.stackDrag.getValue())
        {
            Setting<Integer> setting = getSetting(drag);
            if (setting != null && setting.getValue() == 0)
            {
                return false;
            }

            for (int i = 44; i > 8; i--)
            {
                ItemStack stack = InventoryUtil.get(i);
                if (InventoryUtil.canStack(stack, drag)
                        && stack.getCount() + drag.getCount()
                                <= stack.getMaxStackSize())
                {
                    int finalI = i;
                    Item item = stack.getItem();
                    Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                    {
                        if (InventoryUtil.get(finalI).getItem() == item)
                        {
                            InventoryUtil.click(finalI);
                        }
                    });

                    module.timer.reset();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param map ...
     * @param item ...
     * @param best ...
     * @param smart ...
     * @param stacks ignore when stacks dont have a setting
     * @param max ignore when stacks dont have a setting
     * @return <tt>true</tt> if the best has been clicked.
     */
    @SuppressWarnings("deprecation")
    private boolean findBest(Map<Integer, Integer> map,
                             Item item,
                             Map<Integer, Map.Entry<Integer, Integer>> best,
                             boolean smart,
                             int stacks,
                             int max)
    {
        Set<Integer> checked = new HashSet<>((int) (map.size() * 1.5));
        for (Map.Entry<Integer, Integer> inner : map.entrySet())
        {
            checked.add(inner.getKey());
            for (Map.Entry<Integer, Integer> sec : map.entrySet())
            {
                if (checked.contains(sec.getKey()))
                {
                    continue;
                }

                int diff = item.getItemStackLimit();
                if (inner.getValue() == diff || sec.getValue() == diff)
                {
                    continue;
                }

                diff -= (inner.getValue() + sec.getValue());
                if (diff < 0 && (!smart || stacks <= max))
                {
                    continue;
                }

                // higher key is hotbar so we stack into the hotbar
                int i = Math.max(inner.getKey(), sec.getKey());
                int j = Math.min(inner.getKey(), sec.getKey());

                ItemStack stack1 = InventoryUtil.get(i);
                ItemStack stack2 = InventoryUtil.get(j);
                if (!InventoryUtil.canStack(stack1, stack2))
                {
                    continue;
                }

                if (diff == 0)
                {
                    click(j, i);
                    return true;
                }

                best.put(diff, new AbstractMap.SimpleEntry<>(i,j));
            }
        }

        return false;
    }

    private boolean doXCarry()
    {
        int xCarry = getEmptyXCarry();
        ItemStack drag = mc.player.inventory.getItemStack();
        if (xCarry == -1
            || !drag.isEmpty() && !module.dragCarry.getValue()
            || getSetting(drag) != null)
        {
            return false;
        }

        int stacks = 0;
        Set<Item> invalid = new HashSet<>();
        Map<Item, List<SlotCount>> slots = new HashMap<>();
        for (int i = 44; i > 8; i--)
        {
            ItemStack stack = InventoryUtil.get(i);
            if (stack.isEmpty())
            {
                continue;
            }
            else
            {
                stacks++;
            }

            if (invalid.contains(stack.getItem()))
            {
                continue;
            }

            Setting<Integer> setting = getSetting(stack);
            if (setting == null)
            {
                slots.computeIfAbsent(stack.getItem(), v -> new ArrayList<>())
                     .add(new SlotCount(stack.getCount(), i));
            }
            else
            {
                invalid.add(stack.getItem());
            }
        }

        if (stacks < module.xCarryStacks.getValue())
        {
            return false;
        }

        if (drag.isEmpty())
        {
            int best = -1;
            int bestSize = 0;
            for (Map.Entry<Item, List<SlotCount>> entry : slots.entrySet())
            {
                int size = entry.getValue().size();
                ItemStack deprec = new ItemStack(entry.getKey());
                if (size >= module.minXcarry.getValue() && size > bestSize)
                {
                    for (SlotCount count : entry.getValue())
                    {
                        if (count.getSlot() < 36 // no hotbar!
                                && count.getCount() == deprec.getMaxStackSize())
                        {
                            best = count.getSlot();
                            bestSize = size;
                        }
                    }
                }
            }

            if (best != -1)
            {
                click(best, xCarry);
                return true;
            }
        }
        else
        {
            List<SlotCount> counts = slots.get(drag.getItem());
            if (counts == null && module.minXcarry.getValue() == 0
                || counts != null
                    && counts.size() >= module.minXcarry.getValue())
            {
                Item item = InventoryUtil.get(xCarry).getItem();
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                {
                    if (InventoryUtil.get(xCarry).getItem() == item)
                    {
                        InventoryUtil.click(xCarry);
                    }
                });

                module.timer.reset();
                return true;
            }
        }

        return false;
    }

    private int getEmptyXCarry()
    {
        for (int i = 1; i < 5; i++)
        {
            ItemStack stack = InventoryUtil.get(i);
            if (stack.isEmpty() || stack.getItem() == Items.AIR)
            {
                return i;
            }
        }

        return -1;
    }

    private void click(int first, int second)
    {
        Item firstItem  = InventoryUtil.get(first).getItem();
        Item secondItem = InventoryUtil.get(second).getItem();

        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
        {
            if (InventoryUtil.get(first).getItem() != firstItem
                    || InventoryUtil.get(second).getItem() != secondItem)
            {
                return;
            }

            Managers.NCP.startMultiClick();
            InventoryUtil.click(first);
            InventoryUtil.click(second);
            Managers.NCP.releaseMultiClick();
        });

        module.timer.reset();
    }

    private Setting<Integer> getSetting(ItemStack stack)
    {
        if (!stack.isEmpty() && !module.isStackValid(stack))
        {
            Item item = stack.getItem();
            return module.getSetting(
                    item.getItemStackDisplayName(stack),
                    RemovingInteger.class);
        }

        return null;
    }

    private boolean check(ItemStack stack, int i, Map<Item, ItemToDrop> items)
    {
        if (!stack.isEmpty() && !module.isStackValid(stack))
        {
            Item item = stack.getItem();
            Setting<Integer> setting = module.getSetting(
                    item.getItemStackDisplayName(stack),
                    RemovingInteger.class);

            items.computeIfAbsent(item, v -> new ItemToDrop(setting))
                 .addSlot(i, stack.getCount());
            return true;
        }

        return false;
    }

    private float getYaw(float yaw)
    {
        int same = 0;
        int bestCount = 0;
        EnumFacing bestFacing = null;
        BlockPos pos = PositionUtil.getPosition().up();
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            int count = 0;
            BlockPos current = pos;
            for (int i = 0; i < 5; i++)
            {
                BlockPos offset = current.offset(facing);
                if (mc.world.getBlockState(offset)
                            .getMaterial()
                            .blocksMovement())
                {
                    break;
                }
                else
                {
                    count++;
                }

                current = offset;
            }

            if (count == bestCount || bestFacing == null)
            {
                same++;
            }

            if (count > bestCount)
            {
                bestCount = count;
                bestFacing = facing;
            }
        }

        if (bestFacing == null || same == 4) // same == 4 -> dont care
        {
            return yaw - 180.0f;
        }

        return bestFacing.getHorizontalAngle();
    }

    private boolean isInvFull()
    {
        for (int i = 9; i < 45; i++)
        {
            ItemStack stack = InventoryUtil.get(i);
            if (stack.isEmpty())
            {
                return false;
            }
            else if (stack.getCount() != stack.getMaxStackSize()
                    && module.sizeCheck.getValue())
            {
                for (EntityItem entity : mc.world
                                           .getEntitiesWithinAABB(
                                                EntityItem.class,
                                                RotationUtil
                                                    .getRotationPlayer()
                                                    .getEntityBoundingBox()))
                {
                    if (InventoryUtil.canStack(stack, entity.getItem()))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
