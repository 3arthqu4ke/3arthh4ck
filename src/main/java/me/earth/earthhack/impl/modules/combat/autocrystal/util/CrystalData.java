package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import net.minecraft.entity.Entity;

public class CrystalData implements Comparable<CrystalData>
{
    private final Entity crystal;
    private float selfDmg;
    private float damage;
    private float[] rotations;
    private double angle;

    public CrystalData(Entity crystal)
    {
        this.crystal = crystal;
    }

    public Entity getCrystal()
    {
        return crystal;
    }

    public void setSelfDmg(float damage)
    {
        this.selfDmg = damage;
    }

    public void setDamage(float damage)
    {
        this.damage = damage;
    }

    public float getSelfDmg()
    {
        return selfDmg;
    }

    public float getDamage()
    {
        return damage;
    }

    public float[] getRotations()
    {
        return rotations;
    }

    public double getAngle()
    {
        return angle;
    }

    public boolean hasCachedRotations()
    {
        return rotations != null;
    }

    public void cacheRotations(float[] rotations, double angle)
    {
        this.rotations = rotations;
        this.angle     = angle;
    }

    @Override
    public int compareTo(CrystalData o)
    {
        if (Math.abs(o.damage - this.damage) < 1.0f)
        {
            return Float.compare(this.selfDmg, o.selfDmg);
        }

        return Float.compare(o.damage, this.damage);
    }

    @Override
    public int hashCode()
    {
        // return crystal.getEntityId();
        return crystal.getPosition().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof CrystalData)
        {
            return this.hashCode() == o.hashCode();
        }

        return false;
    }

}
