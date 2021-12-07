package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.item.ItemStack;

public class ProcessRightClickItemEvent
{
    private final ItemStack itemStack;

    public ProcessRightClickItemEvent(ItemStack stack)
    {
        this.itemStack = stack;
    }

    public ItemStack getItemStack()
    {
        return itemStack;
    }

}
