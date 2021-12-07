package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import net.minecraft.entity.Entity;

public class CrystalDataMotion extends CrystalData
{
    private Timing timing = Timing.BOTH;
    private float postSelf;

    public CrystalDataMotion(Entity crystal)
    {
        super(crystal);
    }

    public float getPostSelf()
    {
        return postSelf;
    }

    public Timing getTiming()
    {
        return timing;
    }

    public void setPostSelf(float postSelf)
    {
        this.postSelf = postSelf;
    }

    public void invalidateTiming(Timing timing)
    {
        if (timing == Timing.PRE)
        {
            if (this.timing == Timing.PRE)
            {
                this.timing = Timing.NONE;
            }
            else if (this.timing == Timing.BOTH)
            {
                this.timing = Timing.POST;
            }
        }
        else // if (timing == Timing.POST) basically
        {
            if (this.timing == Timing.POST)
            {
                this.timing = Timing.NONE;
            }
            else if (this.timing == Timing.BOTH)
            {
                this.timing = Timing.PRE;
            }
        }
    }

    @Override
    public int compareTo(CrystalData o)
    {
        if (o instanceof CrystalDataMotion
                && Math.abs(o.getDamage() - this.getDamage()) < 1.0f)
        {
            // -1 means lower selfDamage than other
            CrystalDataMotion motion = (CrystalDataMotion) o;

            boolean breakCase = true;
            float lowestSelf = Float.MAX_VALUE;
            boolean thisBetter = this.getDamage() > o.getDamage();

            switch (motion.getTiming())
            {
                case BOTH:
                    breakCase = false;
                case PRE:
                    if (motion.getSelfDmg() < lowestSelf)
                    {
                        lowestSelf = motion.getSelfDmg();
                        thisBetter = false;
                    }

                    if (breakCase)
                    {
                        break;
                    }
                case POST:
                    if (motion.getPostSelf() < lowestSelf)
                    {
                        lowestSelf = motion.getSelfDmg();
                        thisBetter = false;
                    }

                    break;
                case NONE:
                    return -1;
            }

            breakCase = true;
            switch (this.getTiming())
            {
                case BOTH:
                    breakCase = false;
                case PRE:
                    if (this.getSelfDmg() < lowestSelf
                        || this.getSelfDmg() == lowestSelf
                            && this.getDamage() > motion.getDamage())
                    {
                        lowestSelf = this.getSelfDmg();
                        thisBetter = true;
                    }

                    if (breakCase)
                    {
                        break;
                    }
                case POST:
                    if (this.getSelfDmg() < lowestSelf
                        || this.getSelfDmg() == lowestSelf
                            && this.getDamage() > motion.getDamage())
                    {
                        thisBetter = true;
                    }

                    break;
                case NONE:
                    return 1;
            }

            return thisBetter ? -1 : 1;
        }

        return super.compareTo(o);
    }

    public enum Timing
    {
        NONE,
        PRE,
        POST,
        BOTH
    }

}
