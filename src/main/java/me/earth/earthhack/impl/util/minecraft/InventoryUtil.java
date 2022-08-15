package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class InventoryUtil implements Globals
{
    public static final ItemStack ILLEGAL_STACK =
            new ItemStack(Item.getItemFromBlock(Blocks.BEDROCK));

    public static void switchTo(int slot)
    {
        if (mc.player.inventory.currentItem != slot && slot > -1 && slot < 9)
        {
            mc.player.inventory.currentItem = slot;
            syncItem();
        }
    }

    public static void switchToBypass(int slot)
    {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
        {
            if (mc.player.inventory.currentItem != slot
                && slot > -1 && slot < 9)
            {
                int lastSlot = mc.player.inventory.currentItem;
                int targetSlot = hotbarToInventory(slot);
                int currentSlot = hotbarToInventory(lastSlot);
                mc.playerController
                    .windowClick(0, targetSlot, 0, ClickType.PICKUP,
                                 mc.player);
                mc.playerController
                    .windowClick(0, currentSlot, 0, ClickType.PICKUP,
                                 mc.player);
                mc.playerController
                    .windowClick(0, targetSlot, 0, ClickType.PICKUP,
                                 mc.player);
            }
        });
    }

    /**
     * Bypasses NCP item switch cooldown
     * @param slot INVENTORY SLOT (NOT HOTBAR) to switch to
     */
    public static void switchToBypassAlt(int slot)
    {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
        {
            if (mc.player.inventory.currentItem != slot
                && slot > -1 && slot < 9)
            {
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                    mc.playerController
                        .windowClick(0, slot, mc.player.inventory.currentItem,
                                     ClickType.SWAP, mc.player));
            }
        });
    }

    /**
     * Yet Another Cooldown Bypass... best one yet by far.
     * @param slot hotbar slot to switch to.
     */
    public static void bypassSwitch(int slot)
    {
        if (slot >= 0)
        {
            mc.playerController.pickItem(slot);
        }
    }

    /**
     * {@see https://wiki.vg/index.php?title=Protocol&oldid=14204#Click_Window/}
     */
    public static void illegalSync()
    {
        if (mc.player != null)
        {
            PacketUtil.click(
                0, 0, 0, ClickType.PICKUP, ILLEGAL_STACK, (short) 0);
        }
    }

    public static int findHotbarBlock(Block block, Block...optional)
    {
        return findInHotbar(s -> ItemUtil.areSame(s, block),
            CollectionUtil.convert(optional, o -> s -> ItemUtil.areSame(s, o)));
    }

    public static int findHotbarItem(Item item, Item...optional)
    {
        return findInHotbar(s -> ItemUtil.areSame(s, item),
            CollectionUtil.convert(optional, o -> s -> ItemUtil.areSame(s, o)));
    }

    public static int findInHotbar(Predicate<ItemStack> condition)
    {
        return findInHotbar(condition, true);
    }

    public static int findInHotbar(Predicate<ItemStack> condition,
                                   boolean offhand)
    {
        if (offhand && condition.test(mc.player.getHeldItemOffhand()))
        {
            return -2;
        }

        int result = -1;
        for (int i = 8; i > -1; i--)
        {
            if (condition.test(mc.player.inventory.getStackInSlot(i)))
            {
                result = i;
                if (mc.player.inventory.currentItem == i)
                {
                    break;
                }
            }
        }

        return result;
    }

    public static int findInInventory(Predicate<ItemStack> condition,
                                      boolean xCarry)
    {
        for (int i = 9; i < 45; i++)
        {
            ItemStack stack = mc.player
                    .inventoryContainer
                    .getInventory()
                    .get(i);

            if (condition.test(stack))
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                ItemStack stack = mc.player
                        .inventoryContainer
                        .getInventory()
                        .get(i);

                if (condition.test(stack))
                {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int findInCraftingTable(Container container,
                                          Predicate<ItemStack> condition)
    {
        for (int i = 11; i < 47; i++)
        {
            ItemStack stack = container
                    .getInventory()
                    .get(i);

            if (condition.test(stack))
            {
                return i;
            }
        }

        return -1;
    }

    public static int findInHotbar(Predicate<ItemStack> condition,
                                   Iterable<Predicate<ItemStack>> optional)
    {
        int result = findInHotbar(condition);
        if (result == -1)
        {
            for (Predicate<ItemStack> opt : optional)
            {
                result = findInHotbar(opt);
                if (result != -1)
                {
                    break;
                }
            }
        }

        return result;
    }

    public static int findBlock(Block block, boolean xCarry)
    {
        if (ItemUtil.areSame(mc.player.inventory.getItemStack(), block))
        {
            return -2;
        }

        for (int i = 9; i < 45; i++)
        {
            ItemStack stack = mc.player
                                .inventoryContainer
                                .getInventory()
                                .get(i);

            if (ItemUtil.areSame(stack, block))
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                ItemStack stack = mc.player
                                    .inventoryContainer
                                    .getInventory()
                                    .get(i);

                if (ItemUtil.areSame(stack, block))
                {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int findItem(Item item, Container container)
    {
        for (int i = 0; i < container.getInventory().size(); i++)
        {
            ItemStack stack = container.getInventory().get(i);

            if (stack.getItem() == item)
            {
                return i;
            }
        }

        return -1;
    }

    public static int findItem(Item item, boolean xCarry)
    {
        return findItem(item, xCarry, Collections.emptySet());
    }

    public static int findItem(Item item, boolean xCarry, Set<Integer> ignore)
    {
        if (mc.player.inventory.getItemStack().getItem() == item
                && !ignore.contains(-2))
        {
            return -2;
        }

        for (int i = 9; i < 45; i++)
        {
            if (ignore.contains(i))
            {
                continue;
            }

            if (get(i).getItem() == item)
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                if (ignore.contains(i))
                {
                    continue;
                }

                if (get(i).getItem() == item)
                {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int getCount(Item item)
    {
        int result = 0;
        for (int i = 0; i < 46; i++)
        {
            ItemStack stack = mc.player
                                .inventoryContainer
                                .getInventory()
                                .get(i);

            if (stack.getItem() == item)
            {
                result += stack.getCount();
            }
        }

        if (mc.player.inventory.getItemStack().getItem() == item)
        {
            result += mc.player.inventory.getItemStack().getCount();
        }

        return result;
    }

    public static boolean isHoldingServer(Item item)
    {
        ItemStack offHand  = mc.player.getHeldItemOffhand();
        if (ItemUtil.areSame(offHand, item))
        {
            return true;
        }

        ItemStack mainHand = mc.player.getHeldItemMainhand();
        if (ItemUtil.areSame(mainHand, item))
        {
            int current = mc.player.inventory.currentItem;
            int server  = getServerItem();
            return server == current;
        }

        return false;
    }

    public static boolean isHolding(Class<?> clazz)
    {
        return clazz.isAssignableFrom(
                    mc.player.getHeldItemMainhand().getItem().getClass())
                || clazz.isAssignableFrom(
                        mc.player.getHeldItemOffhand().getItem().getClass());
    }

    public static boolean isHolding(Item item)
    {
        return isHolding(mc.player, item);
    }

    public static boolean isHolding(Block block)
    {
        return isHolding(mc.player, block);
    }

    public static boolean isHolding(EntityLivingBase entity, Item item)
    {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand  = entity.getHeldItemOffhand();

        return ItemUtil.areSame(mainHand, item)
                || ItemUtil.areSame(offHand, item);
    }

    public static boolean isHolding(EntityLivingBase entity, Block block)
    {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand  = entity.getHeldItemOffhand();

        return ItemUtil.areSame(mainHand, block)
                || ItemUtil.areSame(offHand, block);
    }

    /**
     * Returns {@link EnumHand#OFF_HAND} if the given
     * slot is -2 otherwise {@link EnumHand#MAIN_HAND}.
     *
     * @return the Hand for the given slot.
     */
    public static EnumHand getHand(int slot)
    {
        return slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    public static EnumHand getHand(Item item)
    {
        return mc.player.getHeldItemMainhand().getItem() == item
                ? EnumHand.MAIN_HAND
                : mc.player.getHeldItemOffhand().getItem() == item
                    ? EnumHand.OFF_HAND
                    : null;
    }

    /**
     * @return <tt>true</tt> if {@link Minecraft#currentScreen}
     *          is a screen on which we can windowClick all
     *          Slots of the Inventory (with Armor and Offhand).
     */
    public static boolean validScreen()
    {
        return !(mc.currentScreen instanceof GuiContainer)
                || mc.currentScreen instanceof GuiInventory;
    }

    public static int getServerItem()
    {
        return ((IPlayerControllerMP) mc.playerController).getItem();
    }

    /**
     * Syncs the currentItem with the Server.
     * Should always be called while the
     * {@link Locks#PLACE_SWITCH_LOCK} is locked.
     */
    public static void syncItem()
    {
        ((IPlayerControllerMP) mc.playerController).syncItem();
    }

    /**
     * Calls {@link net.minecraft.client.multiplayer.PlayerControllerMP#
     * windowClick(int, int, int, ClickType, EntityPlayer)}
     * for the arguments:
     * <p>-0
     * <p>-the given slot.
     * <p>-0
     * <p>-{@link ClickType#PICKUP}
     * <p>-mc.player
     *
     * @param slot the slot to click.
     */
    public static void click(int slot)
    {
        mc.playerController
          .windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }

    /**
     * @param slot the slot to get.
     * @return {@link Container#getInventory()#get(int)} for the
     *          {@link Minecraft#player}s inventoryContainer.
     */ // TODO: ensure that this is used everywhere where needed
    public static ItemStack get(int slot)
    {
        if (slot == -2)
        {
            return mc.player.inventory.getItemStack();
        }

        return mc.player.inventoryContainer.getInventory().get(slot);
    }

    public static NonNullList<ItemStack> getInventory()
    {
        return mc.player.inventoryContainer.getInventory();
    }

    public static void put(int slot, ItemStack stack)
    {
        if (slot == -2)
        {
            mc.player.inventory.setItemStack(stack);
        }

        mc.player.inventoryContainer.putStackInSlot(slot, stack);

        int invSlot = containerToSlots(slot);
        if (invSlot != -1) {
            mc.player.inventory.setInventorySlotContents(invSlot, stack);
        }
    }

    public static int findEmptyHotbarSlot()
    {
        int result = -1;
        for (int i = 8; i > -1; i--)
        {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() == Items.AIR)
            {
                result = i;
            }
        }

        return result;
    }

    /**
     * Converts a HotbarSlot (for example retrieved by
     * {@link InventoryUtil#findHotbarBlock(Block, Block...)}),
     * to an inventory slot. -2 will be converted to the offhand
     * slot 45. If the slot doesn't belong to the hotbar it will
     * be returned unchanged.
     *
     * @param slot the slot to convert;
     * @return the slot as an inventory slot.
     */
    public static int hotbarToInventory(int slot)
    {
        if (slot == -2)
        {
            return 45;
        }

        if (slot > -1 && slot < 9)
        {
            return 36 + slot;
        }

        return slot;
    }

    /**
     * Returns <tt>true</tt> if the second stack can be
     * placed into the first one. In order for that to happen
     * the first stack needs to be:
     * <p>-empty</p>
     * <p>or</p>
     * <p>-have the same item as the second stack.</p>
     * <p>-have a maxStackSize > 1.</p>
     * <p>-No Subtypes, or the same MetaData as the second stack.</p>
     * <p>-{@link ItemStack#areItemStackTagsEqual(ItemStack, ItemStack)}</p>
     *
     * @param inSlot the stack in the slot
     * @param stack the stack placed inside inSlot.
     * @return <tt>true</tt> if the stacks fit inside each other.
     */
    public static boolean canStack(ItemStack inSlot, ItemStack stack)
    {
        return inSlot.isEmpty()
                || inSlot.getItem() == stack.getItem()
                    && inSlot.getMaxStackSize() > 1
                    && (!inSlot.getHasSubtypes()
                        || inSlot.getMetadata() == stack.getMetadata())
                    && ItemStack.areItemStackTagsEqual(inSlot, stack);
    }

    /**
     * Checks if 2 ItemStacks are equal and ignores the durability.
     * Returns <tt>true</tt> if both are null as well.
     *
     * @param stack1 first stack
     * @param stack2 second stack
     * @return <tt>true</tt> if the stacks are equal.
     */
    public static boolean equals(ItemStack stack1, ItemStack stack2)
    {
        if (stack1 == null)
        {
            return stack2 == null;
        }
        else if (stack2 == null)
        {
            return false;
        }

        boolean empty1 = stack1.isEmpty();
        boolean empty2 = stack2.isEmpty();

        return empty1 == empty2
                && stack1.getDisplayName().equals(stack2.getDisplayName())
                && stack1.getItem() == stack1.getItem()
                && stack1.getHasSubtypes() == stack2.getHasSubtypes()
                && stack1.getMetadata() == stack2.getMetadata()
                && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    /**
     * Locks the {@link Locks#WINDOW_CLICK_LOCK} and
     * performs a (or 2) windowClick(s) with windowId 0 and type Pickup.
     * If the first slot is < 0 it won't be clicked.
     *
     * @param slot the slot to click first. (-2 if drag) (-1 if ignore)
     * @param to the slot to click second.
     * @param inSlot the item in the slot which is clicked first.
     * @param inTo the item in the slot which is clicked second.
     */
    public static void clickLocked(int slot, int to, Item inSlot, Item inTo)
    {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
        {
            if ((slot == -1 || get(slot).getItem() == inSlot)
                    && get(to).getItem() == inTo)
            {
                boolean multi = slot >= 0;
                if (multi)
                {
                    Managers.NCP.startMultiClick();
                    click(slot);
                }

                click(to);

                if (multi)
                {
                    Managers.NCP.releaseMultiClick();
                }
            }
        });
    }

    /**
     * Converts slots from {@link Container} to slots from
     * {@link InventoryPlayer}. Crafting slots are not part of the
     * {@link InventoryPlayer}! For those slots -1 will be returned.
     *
     * @param containerSlot the slot in the container.
     * @return the slot in the Inventory or -1 if not in Inventory.
     */
    public static int containerToSlots(int containerSlot) {
        if (containerSlot < 5 || containerSlot > 45) { // crafting slots
            return -1;
        }

        if (containerSlot <= 9) {
            return 44 - containerSlot;
        }

        if (containerSlot < 36) {
            return containerSlot;
        }

        if (containerSlot < 45) {
            return containerSlot - 36;
        }

        return 40; // offhand is 40 here
    }

}
