package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import net.minecraft.entity.player.EntityPlayer;

import java.util.Set;
import java.util.TreeSet;

public class AntiTotemData extends PositionData
{
    private final Set<PositionData> corresponding = new TreeSet<>();

    public AntiTotemData(PositionData data)
    {
        super(data.getPos(), data.getMaxLength(), data.getAntiTotems());
    }

    public void addCorrespondingData(PositionData data)
    {
        this.corresponding.add(data);
    }

    public Set<PositionData> getCorresponding()
    {
        return corresponding;
    }

    @Override
    public int compareTo(PositionData o)
    {
        if (Math.abs(o.getSelfDamage() - this.getSelfDamage()) < 1.0f
                && o instanceof AntiTotemData)
        {
            EntityPlayer player = getFirstTarget();
            EntityPlayer other  = ((AntiTotemData) o).getFirstTarget();

            if (other == null)
            {
                if (player == null)
                {
                    return super.compareTo(o);
                }

                return -1; // this ones better
            }
            else
            {
                if (player == null)
                {
                    return 1; // other is better
                }

                return Double.compare(player.getDistanceSq(this.getPos()),
                                      other.getDistanceSq(o.getPos()));
            }
        }

        return super.compareTo(o);
    }

    public EntityPlayer getFirstTarget()
    {
        return getAntiTotems().stream().findFirst().orElse(null);
    }

}
