package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.BreakData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalDataMotion;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.IBreakHelper;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

// TODO: Also use this if we Multithread and use Rotations?
public class CalculationMotion extends AbstractCalculation<CrystalDataMotion>
{
    public CalculationMotion(AutoCrystal module,
                             List<Entity> entities,
                             List<EntityPlayer> players)
    {
        super(module, entities, players);
    }

    @Override
    protected IBreakHelper<CrystalDataMotion> getBreakHelper()
    {
        return module.breakHelperMotion;
    }

    @Override
    protected boolean evaluate(BreakData<CrystalDataMotion> breakData)
    {
        // count = breakData.getData().size();
        boolean slowReset = false;
        BreakValidity validity;
        if (this.breakData.getAntiTotem() != null
                && (validity =
                HelperUtil.isValid(module, this.breakData.getAntiTotem()))
                != BreakValidity.INVALID)
        {
            attack(this.breakData.getAntiTotem(), validity);
            module.breakTimer.reset(module.breakDelay.getValue());
            module.antiTotemHelper.setTarget(null);
            module.antiTotemHelper.setTargetPos(null);
        }
        else
        {
            int packets = !module.rotate.getValue().noRotate(ACRotate.Break)
                    ? 1
                    : module.packets.getValue();

            CrystalDataMotion firstRotation = null;
            List<CrystalDataMotion> valids = new ArrayList<>(packets);
            for (CrystalDataMotion data : this.breakData.getData())
            {
                if (data.getTiming() == CrystalDataMotion.Timing.NONE)
                {
                    continue;
                }

                validity = isValid(module, data);
                if (validity == BreakValidity.VALID && valids.size() < packets)
                {
                    valids.add(data);
                }
                else if (validity == BreakValidity.ROTATIONS
                    && (data.getTiming() == CrystalDataMotion.Timing.BOTH
                        || data.getTiming() == CrystalDataMotion.Timing.POST)
                    && firstRotation == null)
                {
                    firstRotation = data;
                }
            }

            int slowDelay = module.slowBreakDelay.getValue();
            float slow = module.slowBreakDamage.getValue();
            if (valids.isEmpty())
            {
                if (firstRotation != null
                        && (module.shouldDanger()
                            || !(slowReset = firstRotation.getDamage() <= slow)
                            || module.breakTimer.passed(slowDelay)))
                {
                    attack(firstRotation.getCrystal(),
                            BreakValidity.ROTATIONS);
                }
            }
            else
            {
                slowReset = !module.shouldDanger();
                for (CrystalDataMotion v : valids)
                {
                    boolean high = v.getDamage()
                            > module.slowBreakDamage.getValue();
                    if (high || module.breakTimer
                                      .passed(module.slowBreakDelay.getValue()))
                    {
                        slowReset = slowReset && !high;
                        if (v.getTiming() == CrystalDataMotion.Timing.POST
                            || v.getTiming() == CrystalDataMotion.Timing.BOTH
                                    && v.getPostSelf() < v.getSelfDmg())
                        {
                            attackPost(v.getCrystal());
                        }
                        else
                        {
                            attack(v.getCrystal(), BreakValidity.VALID);
                        }
                    }
                }
            }
        }

        if (attacking)
        {
            module.breakTimer.reset(slowReset
                    ? module.slowBreakDelay.getValue()
                    : module.breakDelay.getValue());
        }

        return rotating && !module.rotate.getValue().noRotate(ACRotate.Place);
    }

    protected void attackPost(Entity entity)
    {
        attacking = true;
        scheduling = true;
        rotating = !module.rotate.getValue().noRotate(ACRotate.Break);
        MutableWrapper<Boolean> attacked = new MutableWrapper<>(false);
        Runnable post = module.rotationHelper.post(entity, attacked);
        module.post.add(post);
    }

    private BreakValidity isValid(AutoCrystal module,
                                  CrystalDataMotion dataMotion)
    {
        Entity crystal = dataMotion.getCrystal();
        if (module.existed.getValue() != 0
                && System.currentTimeMillis()
                    - ((IEntity) crystal).getTimeStamp()
                        + (module.pingExisted.getValue()
                            ? ServerUtil.getPingNoPingSpoof() / 2.0
                            : 0)
                    < module.existed.getValue())
        {
            return BreakValidity.INVALID;
        }

        if (module.rotate.getValue().noRotate(ACRotate.Break)
            || (RotationUtil.isLegit(crystal, crystal)
                && module.positionHistoryHelper
                         .arePreviousRotationsLegit(crystal,
                            module.rotationTicks
                                  .getValue(),
                            true)))
        {
            return BreakValidity.VALID;
        }

        return BreakValidity.ROTATIONS;
    }

}
