package me.earth.earthhack.impl.modules.combat.autocrystal.util;

public class CrystalTimeStamp extends TimeStamp
{
    private final float damage;

    public CrystalTimeStamp(float damage)
    {
        this.damage = damage;
    }

    public float getDamage()
    {
        return damage;
    }

}
