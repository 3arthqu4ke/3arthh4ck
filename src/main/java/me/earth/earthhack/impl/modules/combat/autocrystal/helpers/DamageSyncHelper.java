package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.util.math.BlockPos;

public class DamageSyncHelper
{
    private final DiscreteTimer discreteTimer = new GuardTimer(1000, 5);
    private final StopWatch timer = new StopWatch();
    private final Setting<Integer> syncDelay;
    private final Setting<Boolean> discrete;
    private final Setting<Boolean> danger;
    private final Confirmer confirmer;

    private float lastDamage;

    public DamageSyncHelper(EventBus eventBus,
                            Setting<Boolean> discrete,
                            Setting<Integer> syncDelay,
                            Setting<Boolean> danger)
    {
        this.danger = danger;
        this.confirmer = Confirmer.createAndSubscribe(eventBus);
        this.syncDelay = syncDelay;
        this.discrete  = discrete;
        this.discreteTimer.reset(syncDelay.getValue());
    }

    public void setSync(BlockPos pos, float damage, boolean newVer)
    {
        int placeTime = (int) (ServerUtil.getPingNoPingSpoof() / 2.0 + 1);
        confirmer.setPos(pos.toImmutable(), newVer, placeTime);
        lastDamage = damage;

        if (discrete.getValue() && discreteTimer.passed(syncDelay.getValue()))
        {
            discreteTimer.reset(syncDelay.getValue());
        }
        else if (!discrete.getValue() && timer.passed(syncDelay.getValue()))
        {
            timer.reset();
        }
    }

    public boolean isSyncing(float damage,
                             boolean damageSync)
    {
        return damageSync
                && (!danger.getValue() || Managers.SAFETY.isSafe())
                && confirmer.isValid()
                && damage <= lastDamage
                && (discrete.getValue()
                        && !discreteTimer.passed(syncDelay.getValue())
                    || !discrete.getValue()
                        && !timer.passed(syncDelay.getValue()));
    }

    public Confirmer getConfirmer()
    {
        return confirmer;
    }

}
