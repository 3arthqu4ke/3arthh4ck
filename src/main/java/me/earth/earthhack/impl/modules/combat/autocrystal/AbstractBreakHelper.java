package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.BreakData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.IBreakHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.List;

public abstract class AbstractBreakHelper<T extends CrystalData>
        implements IBreakHelper<T>, Globals
{
    protected final AutoCrystal module;

    public AbstractBreakHelper(AutoCrystal module)
    {
        this.module = module;
    }

    protected abstract T newCrystalData(Entity crystal);

    protected abstract boolean isValid(Entity crystal, T data);

    protected abstract boolean calcSelf(
        BreakData<T> breakData,
        Entity crystal, T data);

    protected abstract void calcCrystal(BreakData<T> data,
                                        T crystalData,
                                        Entity crystal,
                                        List<EntityPlayer> players);

    @Override
    public BreakData<T> getData(Collection<T> dataSet,
                                List<Entity> entities,
                                List<EntityPlayer> players,
                                List<EntityPlayer> friends)
    {
        BreakData<T> data = newData(dataSet);
        for (Entity crystal : entities)
        {
            if (!(crystal instanceof EntityEnderCrystal)
                    || EntityUtil.isDead(crystal)
                        && (!module.countDeadCrystals.getValue()
                            || module.countDeathTime.getValue()
                                && (crystal.isDead && Managers.SET_DEAD.passedDeathTime(crystal, module.getDeathTime())
                                    || ((IEntity) crystal).isPseudoDead() && ((IEntity) crystal).getPseudoTime().passed(module.getDeathTime()))))
            {
                continue;
            }

            T crystalData = newCrystalData(crystal);
            if (calcSelf(data, crystal, crystalData))
            {
                continue;
            }

            if (!isValid(crystal, crystalData)
                || module.antiFriendPop.getValue()
                                       .shouldCalc(AntiFriendPop.Break)
                    && checkFriendPop(crystal, friends))
            {
                continue;
            }

            calcCrystal(data, crystalData, crystal, players);
        }

        return data;
    }

    protected boolean checkFriendPop(Entity entity, List<EntityPlayer> friends)
    {
        for (EntityPlayer friend : friends)
        {
            float fDamage = module.damageHelper.getDamage(entity, friend);
            if (fDamage > EntityUtil.getHealth(friend) - 1.0f)
            {
                return true;
            }
        }

        return false;
    }

}
