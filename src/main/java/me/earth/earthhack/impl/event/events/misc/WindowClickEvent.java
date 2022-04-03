package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

public class WindowClickEvent extends Event {
    private final int windowId, slotId, mouseButton;
    private final ClickType type;
    private final EntityPlayer player;
    private ItemStack stack = ItemStack.EMPTY;

    public WindowClickEvent(int windowId, int slotId, int mouseButton,
                            ClickType type,
                            EntityPlayer player)
    {
        this.windowId = windowId;
        this.slotId = slotId;
        this.mouseButton = mouseButton;
        this.type = type;
        this.player = player;
    }

    public int getWindowId()
    {
        return windowId;
    }

    public int getSlotId()
    {
        return slotId;
    }

    public int getMouseButton()
    {
        return mouseButton;
    }

    public ClickType getType()
    {
        return type;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }
}
