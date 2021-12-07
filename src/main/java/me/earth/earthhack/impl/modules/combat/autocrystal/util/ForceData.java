package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import java.util.Set;
import java.util.TreeSet;

/**
 * ForceData is Data for AntiTotem-Force. Positions
 * in this data will deal just the right amount of damage
 * that will cause the target to be in AntiTotem range.
 * In order for ForceData to be valid an AntiTotem and
 * a position that pops the player afterwards are needed.
 */
public class ForceData
{
    private final Set<ForcePosition> forceData = new TreeSet<>();
    private boolean possibleHighDamage;
    private boolean possibleAntiTotem;

    public boolean hasPossibleHighDamage()
    {
        return possibleHighDamage;
    }

    public void setPossibleHighDamage(boolean possibleHighDamage)
    {
        this.possibleHighDamage = possibleHighDamage;
    }

    public boolean hasPossibleAntiTotem()
    {
        return possibleAntiTotem;
    }

    public void setPossibleAntiTotem(boolean possibleAntiTotem)
    {
        this.possibleAntiTotem = possibleAntiTotem;
    }

    public Set<ForcePosition> getForceData()
    {
        return forceData;
    }

}
