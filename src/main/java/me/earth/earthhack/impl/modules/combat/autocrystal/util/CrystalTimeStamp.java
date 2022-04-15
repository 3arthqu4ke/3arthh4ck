package me.earth.earthhack.impl.modules.combat.autocrystal.util;

public class CrystalTimeStamp extends TimeStamp
{
    private final float damage;
    private final boolean shield;

    public CrystalTimeStamp(float damage, boolean shield)
    {
        this.damage = damage;
        this.shield = shield;
    }

    public float getDamage()
    {
        return damage;
    }

    public boolean isShield() {
        return shield;
    }
}
