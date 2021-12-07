package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EatEvent
{
    private final ItemStack stack;
    private final EntityLivingBase entity;

    public EatEvent(ItemStack stack, EntityLivingBase entity)
    {
        this.stack = stack;
        this.entity = entity;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public EntityLivingBase getEntity()
    {
        return entity;
    }

}
