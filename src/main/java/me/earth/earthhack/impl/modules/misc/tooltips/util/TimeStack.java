package me.earth.earthhack.impl.modules.misc.tooltips.util;

import net.minecraft.item.ItemStack;

public class TimeStack
{
    private final ItemStack stack;
    private final long time;

    public TimeStack(ItemStack stack, long time)
    {
        this.stack = stack;
        this.time  = time;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public long getTime()
    {
        return time;
    }

}
