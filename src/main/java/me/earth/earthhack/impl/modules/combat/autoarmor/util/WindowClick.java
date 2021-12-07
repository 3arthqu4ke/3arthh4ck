package me.earth.earthhack.impl.modules.combat.autoarmor.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class WindowClick implements Globals
{
    private final int slot;
    private final int drag;
    private final int target;
    private final ItemStack inSlot;
    private final ItemStack inDrag;
    private final Consumer<PlayerControllerMP> action;
    private boolean doubleClick;
    private Runnable post;
    private boolean fast;

    public WindowClick(int slot, ItemStack inSlot, ItemStack inDrag)
    {
        this(slot, inSlot, inDrag, slot);
    }

    public WindowClick(int slot, ItemStack inSlot, ItemStack inDrag, int target)
    {
        this(slot, inSlot, inDrag, target,
                p -> p.windowClick(0, slot, 0, ClickType.PICKUP, mc.player));
    }

    public WindowClick(int slot,
                       ItemStack inSlot,
                       ItemStack inDrag,
                       int target,
                       Consumer<PlayerControllerMP> action)
    {
        this(slot, inSlot, -2, inDrag, target, action);
    }

    public WindowClick(int slot,
                       ItemStack inSlot,
                       int dragSlot,
                       ItemStack inDrag,
                       int target,
                       Consumer<PlayerControllerMP> action)
    {
        this.slot = slot;
        this.inSlot = inSlot;
        this.inDrag = inDrag;
        this.target = target;
        this.action = action;
        this.drag = dragSlot;
    }

    public void runClick(PlayerControllerMP controller)
    {
        ItemStack stack = null;
        ItemStack drag  = null;

        if (slot != -1 && slot != -999)
        {
            stack = InventoryUtil.get(slot);
            drag  = mc.player.inventory.getItemStack();
        }

        action.accept(controller);

        if (slot != -1
            && slot != -999
            && fast
            && (!InventoryUtil.equals(stack, mc.player.inventory.getItemStack())
                || !InventoryUtil.equals(drag, InventoryUtil.get(slot))))
        {
            InventoryUtil.put(slot, mc.player.inventory.getItemStack());
            mc.player.inventory.setItemStack(stack);
        }

        if (post != null)
        {
            post.run();
        }
    }

    public boolean isValid()
    {
        if (mc.player != null)
        {
            ItemStack stack = InventoryUtil.get(drag);
            if (InventoryUtil.equals(stack, inDrag))
            {
                if (slot < 0)
                {
                    return true;
                }

                stack = InventoryUtil.get(slot);
                return InventoryUtil.equals(stack, inSlot);
            }
        }

        return false;
    }

    public int getSlot()
    {
        return slot;
    }

    public int getTarget()
    {
        return target;
    }

    public boolean isDoubleClick()
    {
        return doubleClick;
    }

    public void setDoubleClick(boolean doubleClick)
    {
        this.doubleClick = doubleClick;
    }

    public void addPost(Runnable post)
    {
        this.post = post;
    }

    public void setFast(boolean fast)
    {
        this.fast = fast;
    }

}
