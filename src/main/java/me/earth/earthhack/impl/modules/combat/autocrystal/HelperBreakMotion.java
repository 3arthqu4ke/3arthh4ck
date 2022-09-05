package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.BreakData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalDataMotion;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.List;

public class HelperBreakMotion extends AbstractBreakHelper<CrystalDataMotion>
{
    private static final SettingCache<Float, NumberSetting<Float>, Safety> MD =
            Caches.getSetting(Safety.class, Setting.class, "MaxDamage", 4.0f);

    public HelperBreakMotion(AutoCrystal module)
    {
        super(module);
    }

    @Override
    public BreakData<CrystalDataMotion> newData(Collection<CrystalDataMotion> data)
    {
        return new BreakData<>(data);
    }

    @Override
    protected CrystalDataMotion newCrystalData(Entity crystal)
    {
        return new CrystalDataMotion(crystal);
    }

    @Override
    protected boolean isValid(Entity crystal, CrystalDataMotion data)
    {
        double distance = Managers.POSITION.getDistanceSq(crystal);
        if (!module.rangeHelper.isCrystalInRangeOfLastPosition(crystal)
            || distance >= MathUtil.square(module.breakTrace.getValue())
                && !Managers.POSITION.canEntityBeSeen(crystal))
        {
            data.invalidateTiming(CrystalDataMotion.Timing.PRE);
        }

        EntityPlayer e = RotationUtil.getRotationPlayer();
        distance = e.getDistanceSq(crystal);
        if (!module.rangeHelper.isCrystalInRange(crystal)
            || distance >= MathUtil.square(module.breakTrace.getValue())
                && !e.canEntityBeSeen(crystal))
        {
            data.invalidateTiming(CrystalDataMotion.Timing.POST);
        }

        return data.getTiming() != CrystalDataMotion.Timing.NONE;
    }

    @Override
    protected boolean calcSelf(
        BreakData<CrystalDataMotion> breakData,
        Entity crystal, CrystalDataMotion data)
    {
        boolean breakCase = true;
        boolean incrementedCount = false;
        switch (data.getTiming())
        {
            case BOTH:
                breakCase = false;
            case PRE:
                float preDamage = module.damageHelper.getDamage(crystal);
                if (preDamage <= module.shieldSelfDamage.getValue())
                {
                    breakData.setShieldCount(breakData.getShieldCount() + 1);
                    incrementedCount = true;
                }

                data.setSelfDmg(preDamage);
                if (preDamage > EntityUtil.getHealth(mc.player) - 1.0f)
                {
                    if (!module.suicide.getValue())
                    {
                        data.invalidateTiming(CrystalDataMotion.Timing.PRE);
                    }
                }

                if (breakCase)
                {
                    break;
                }
            case POST:
                float postDamage = module.damageHelper.getDamage(
                        crystal, RotationUtil.getRotationPlayer()
                                             .getEntityBoundingBox());
                if (!incrementedCount
                    && postDamage <= module.shieldSelfDamage.getValue())
                {
                    breakData.setShieldCount(breakData.getShieldCount() + 1);
                }

                data.setPostSelf(postDamage);
                if (postDamage > EntityUtil.getHealth(mc.player) - 1.0f)
                {
                    Managers.SAFETY.setSafe(false);
                    if (!module.suicide.getValue())
                    {
                        data.invalidateTiming(CrystalDataMotion.Timing.POST);
                    }
                }

                if (postDamage > MD.getValue())
                {
                    Managers.SAFETY.setSafe(false);
                }

                break;
            default:
        }

        return data.getTiming() == CrystalDataMotion.Timing.NONE;
    }

    @Override
    protected void calcCrystal(BreakData<CrystalDataMotion> data,
                               CrystalDataMotion crystalData,
                               Entity crystal,
                               List<EntityPlayer> players)
    {
        boolean highPreSelf  = crystalData.getSelfDmg()
                                > module.maxSelfBreak.getValue();
        boolean highPostSelf = crystalData.getPostSelf()
                                > module.maxSelfBreak.getValue();

        if (!module.suicide.getValue()
                && !module.overrideBreak.getValue()
                && highPreSelf
                && highPostSelf)
        {
            crystalData.invalidateTiming(CrystalDataMotion.Timing.PRE);
            crystalData.invalidateTiming(CrystalDataMotion.Timing.POST);
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
                highPreSelf = false;
                highPostSelf = false;
                killing = true;
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

        if (highPreSelf)
        {
            crystalData.invalidateTiming(CrystalDataMotion.Timing.PRE);
        }

        if (highPostSelf)
        {
            crystalData.invalidateTiming(CrystalDataMotion.Timing.POST);
        }

        if (crystalData.getTiming() != CrystalDataMotion.Timing.NONE
            && (!module.efficient.getValue()
                    || damage > crystalData.getSelfDmg())
                    || killing)
        {
            data.register(crystalData);
        }
    }

}
