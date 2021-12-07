package me.earth.earthhack.impl.modules.combat.autoarmor.util;

import net.minecraft.item.ItemStack;

public class LevelStack extends DamageStack
{
    private final int level;

    public LevelStack(ItemStack stack, float damage, int slot, int level)
    {
        super(stack, damage, slot);
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }

    public boolean isBetter(float damage, float min, int level, boolean prio)
    {
        if (level > this.level)
        {
            return false;
        }
        else if (level < this.level)
        {
            return true;
        }

        if (prio)
        {
            return !(damage > min) || !(damage < this.getDamage());
        }

        return !(damage > this.getDamage());
    }

}
