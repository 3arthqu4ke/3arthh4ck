package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;

final class ListenerGameLoop extends ModuleListener<AutoCrystal, GameLoopEvent>
{
    public ListenerGameLoop(AutoCrystal module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        module.rotationCanceller.onGameLoop();
        if (!module.multiThread.getValue()) {
            return;
        }

        if (module.gameloop.getValue()
            && module.pullTimer.passed(module.pullBasedDelay.getValue()))
        {
            module.threadHelper.startThread();
            module.pullTimer.reset();
        }
        else if (module.rotate.getValue() != ACRotate.None
            && module.rotationThread.getValue() == RotationThread.Predict
            && mc.getRenderPartialTicks() >= module.partial.getValue())
        {
            module.threadHelper.startThread();
        }
        else if (module.rotate.getValue() == ACRotate.None
                && module.serverThread.getValue()
                && mc.world != null
                && mc.player != null)
        {
            if (Managers.TICK.valid(
                    Managers.TICK.getTickTimeAdjusted(),
                    Managers.TICK.normalize(Managers.TICK.getSpawnTime()
                            - module.tickThreshold.getValue()),
                    Managers.TICK.normalize(Managers.TICK.getSpawnTime()
                            - module.preSpawn.getValue())))
            {
                if (!module.earlyFeetThread.getValue())
                {
                    module.threadHelper.startThread();
                }
                else if (module.lateBreakThread.getValue())
                {
                    module.threadHelper.startThread(true, false);
                }
            }
            else if (EntityUtil.getClosestEnemy() != null
                    && BlockUtil.isSemiSafe(EntityUtil.getClosestEnemy(), true, module.newVer.getValue())
                    && BlockUtil.canBeFeetPlaced(EntityUtil.getClosestEnemy(), true, module.newVer.getValue()) // temp and hacky
                    && module.earlyFeetThread.getValue()
                    && Managers.TICK.valid(Managers.TICK.getTickTimeAdjusted(), 0, module.maxEarlyThread.getValue()))
            {
                module.threadHelper.startThread(false, true);
            }
        }
    }

}
