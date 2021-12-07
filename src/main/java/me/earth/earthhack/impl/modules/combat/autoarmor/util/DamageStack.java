package me.earth.earthhack.impl.modules.combat.autoarmor.util;

import net.minecraft.item.ItemStack;

/**
 * For AutoArmor. A comparable representing an ArmorStack.
 * Contains slot and damage to the armor. Returns 0 when
 * compared to a DamageStack with the same Damage, 1 if
 * that DamageStack is less damaged than this one and -1 otherwise.
 * Being less damaged means a higher damage value in this case.
 * Generally this is done because we want to take off the least
 * damaged Stacks first when AutoMending.
 */
public class DamageStack implements Comparable<DamageStack>
{
    private final ItemStack stack;
    private final float damage;
    private final int slot;

    public DamageStack(ItemStack stack, float damage, int slot)
    {
        this.stack = stack;
        this.damage = damage;
        this.slot = slot;
    }

    public int getSlot()
    {
        return slot;
    }

    public float getDamage()
    {
        return damage;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    public int compareTo(DamageStack o)
    {
        return Float.compare(o.damage, this.damage);
    }

}
