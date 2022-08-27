package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Set;
import java.util.TreeSet;

public class AntiTotemData extends PositionData
{
    private final Set<PositionData> corresponding = new TreeSet<>();

    public AntiTotemData(PositionData data, AutoCrystal module)
    {
        super(data.getPos(), data.getMaxLength(), module, data.getAntiTotems());
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
                return player == null ? super.compareTo(o) : -1;
            }
            else
            {
                return player == null ? 1 : Double.compare(player.getDistanceSq(this.getPos()),
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
