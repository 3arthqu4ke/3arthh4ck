package me.earth.earthhack.impl.util.thread;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;

/**
 * Utility for {@link Enchantment}s.
 */
public class EnchantmentUtil
{
    /**
     * The part of minecraft's EnchantmentHelper needed
     * to calculate Explosion Damage for AutoCrystal etc..
     * But implemented in a way that allows you to access
     * it from multiple threads at the same time. Still not
     * safe regarding the list of item stacks.
     *
     * @param stacks the stacks to check.
     * @param source the damage source.
     */
    public static int getEnchantmentModifierDamage(Iterable<ItemStack> stacks,
                                                   DamageSource source)
    {
        int modifier = 0;
        for (ItemStack stack : stacks)
        {
            if (!stack.isEmpty())
            {
                NBTTagList nbttaglist = stack.getEnchantmentTagList();
                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    int id = nbttaglist.getCompoundTagAt(i).getShort("id");
                    int lvl = nbttaglist.getCompoundTagAt(i).getShort("lvl");
                    Enchantment ench = Enchantment.getEnchantmentByID(id);
                    if (ench != null)
                    {
                        modifier += ench.calcModifierDamage(lvl, source);
                    }
                }
            }
        }

        return modifier;
    }

    /**
     * Enchants the given stack with the enchantment represented
     * by the given id, and the given level.
     *
     * @param stack the stack to enchant.
     * @param id the id for the enchantment (Will be casted to a short).
     * @param level the level for the enchantment (Will be casted to a short).
     * @throws NullPointerException if no Enchantment for the id is found.
     */
    public static void addEnchantment(ItemStack stack, int id, int level)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (!stack.getTagCompound().hasKey("ench", 9))
        {
            stack.getTagCompound().setTag("ench", new NBTTagList());
        }

        NBTTagList nbttaglist = stack.getTagCompound().getTagList("ench", 10);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setShort("id",  (short) id);
        nbttagcompound.setShort("lvl", (short) level);
        nbttaglist.appendTag(nbttagcompound);
    }

}
