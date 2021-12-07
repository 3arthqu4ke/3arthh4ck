package me.earth.earthhack.impl.modules.combat.autocrystal.util;

public class MineSlots
{
    private final int blockSlot;
    private final int toolSlot;
    private final float damage;

    public MineSlots(int blockSlot, int toolSlot, float damage)
    {
        this.blockSlot = blockSlot;
        this.toolSlot = toolSlot;
        this.damage = damage;
    }

    public int getBlockSlot()
    {
        return blockSlot;
    }

    public int getToolSlot()
    {
        return toolSlot;
    }

    public float getDamage()
    {
        return damage;
    }

}
