package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.item.ItemStack;

public class ToolTipEvent extends Event
{
    private final ItemStack stack;
    private final int x;
    private final int y;

    public ToolTipEvent(ItemStack stack, int x, int y)
    {
        this.stack = stack;
        this.x = x;
        this.y = y;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public static class Post extends ToolTipEvent
    {
        public Post(ItemStack stack, int x, int y)
        {
            super(stack, x, y);
        }
    }

}
