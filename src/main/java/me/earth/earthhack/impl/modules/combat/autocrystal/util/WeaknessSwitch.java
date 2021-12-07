package me.earth.earthhack.impl.modules.combat.autocrystal.util;

public class WeaknessSwitch
{
    public static final WeaknessSwitch NONE    = new WeaknessSwitch(-1, false);
    public static final WeaknessSwitch INVALID = new WeaknessSwitch(-1, true);

    private final int slot;
    private final boolean needsSwitch;

    public WeaknessSwitch(int slot, boolean needsSwitch)
    {
        this.slot = slot;
        this.needsSwitch = needsSwitch;
    }

    public int getSlot()
    {
        return slot;
    }

    public boolean needsSwitch()
    {
        return needsSwitch;
    }

}
