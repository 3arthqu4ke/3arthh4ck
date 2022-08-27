package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiWeakness;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;

public class WeaknessHelper
{
    private final Setting<AntiWeakness> antiWeakness;
    private final Setting<Integer> cooldown;
    private boolean weaknessed;

    public WeaknessHelper(Setting<AntiWeakness> antiWeakness,
                          Setting<Integer> cooldown)
    {
        this.antiWeakness = antiWeakness;
        this.cooldown     = cooldown;
    }

    /**
     * Updates if we are weaknessed. We poll this since
     * we multithread and don't want problems with the
     * PotionMap.
     */
    public void updateWeakness()
    {
        weaknessed = !DamageUtil.canBreakWeakness(true);
    }

    /**
     * @return <tt>true</tt> if we are weaknessed.
     */
    public boolean isWeaknessed()
    {
        return weaknessed;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canSwitch()
    {
        return antiWeakness.getValue() == AntiWeakness.Switch
                && cooldown.getValue() == 0
                && weaknessed;
    }

}
