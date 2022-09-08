package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.BreakData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.List;

public class HelperBreak extends AbstractBreakHelper<CrystalData>
{
    private static final SettingCache<Float, NumberSetting<Float>, Safety> MD =
        Caches.getSetting(Safety.class, Setting.class, "MaxDamage", 4.0f);

    public HelperBreak(AutoCrystal module)
    {
        super(module);
    }

    @Override
    public BreakData<CrystalData> newData(Collection<CrystalData> data)
    {
        return new BreakData<>(data);
    }

    @Override
    protected CrystalData newCrystalData(Entity crystal)
    {
        return new CrystalData(crystal);
    }

    @Override
    protected boolean isValid(Entity crystal, CrystalData data)
    {
        double distance = Managers.POSITION.getDistanceSq(crystal);
        if (distance > MathUtil.square(module.breakTrace.getValue())
                && !Managers.POSITION.canEntityBeSeen(crystal))
        {
            return false;
        }

        return module.rangeHelper.isCrystalInRangeOfLastPosition(crystal);
    }

    @Override
    protected boolean calcSelf(
        BreakData<CrystalData> breakData,
        Entity crystal, CrystalData data)
    {
        float selfDamage = module.damageHelper.getDamage(crystal);
        data.setSelfDmg(selfDamage);
        if (selfDamage <= module.shieldSelfDamage.getValue())
        {
            breakData.setShieldCount(breakData.getShieldCount() + 1);
        }

        if (selfDamage > EntityUtil.getHealth(mc.player) - 1.0f)
        {
            Managers.SAFETY.setSafe(false);
            if (!module.suicide.getValue())
            {
                return true;
            }
        }

        if (selfDamage > MD.getValue())
        {
            Managers.SAFETY.setSafe(false);
        }

        return false;
    }

    @Override
    protected void calcCrystal(BreakData<CrystalData> data,
                               CrystalData crystalData,
                               Entity crystal,
                               List<EntityPlayer> players)
    {
        boolean highSelf = crystalData.getSelfDmg()
                                > module.maxSelfBreak.getValue();

        if (!module.suicide.getValue()
                && !module.overrideBreak.getValue()
                && highSelf)
        {
            return;
        }

        float damage = 0.0f;
        boolean killing = false;
        for (EntityPlayer player : players)
        {
            if (player.getDistanceSq(crystal) > 144)
            {
                continue;
            }

            float playerDamage = module.damageHelper.getDamage(crystal, player);
            if (playerDamage > crystalData.getDamage())
            {
                crystalData.setDamage(playerDamage);
            }

            if (playerDamage > EntityUtil.getHealth(player) + 1.0f)
            {
                killing = true;
                highSelf = false;
            }

            if (playerDamage > damage)
            {
                damage = playerDamage;
            }
        }

        if (module.antiTotem.getValue()
                && !EntityUtil.isDead(crystal)
                && crystal.getPosition()
                          .down()
                          .equals(module.antiTotemHelper.getTargetPos()))
        {
            data.setAntiTotem(crystal);
        }

        if (!highSelf && (!module.efficient.getValue()
                            || damage > crystalData.getSelfDmg()
                            || killing))
        {
            data.register(crystalData);
        }
    }

}
