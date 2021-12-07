package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.timer.mode.TimerMode;
import me.earth.earthhack.impl.util.network.PhysicsUtil;

final class ListenerMotion extends ModuleListener<Timer, MotionUpdateEvent>
{
    private float offset = 0.0004f;

    public ListenerMotion(Timer module)
    {
        super(module, MotionUpdateEvent.class, -500000);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            if (module.mode.getValue() == TimerMode.Blink)
            {
                if (event.getPitch() - ((IEntityPlayerSP) mc.player).getLastReportedPitch() == 0.0
                     && event.getYaw() - ((IEntityPlayerSP) mc.player).getLastReportedYaw() == 0.0)
                {
                    offset = -offset;
                    event.setYaw(event.getYaw() + offset);
                    event.setPitch(event.getPitch() + offset);
                }
            }
        }
        else if (event.getStage() == Stage.POST)
        {
            if (module.autoOff.getValue() != 0
                    && module.offTimer.passed(module.autoOff.getValue()))
            {
                module.disable();
                return;
            }

            if (module.ticks < module.updates.getValue()
                    && module.mode.getValue() == TimerMode.Physics)
            {
                module.ticks++;
                PhysicsUtil.runPhysicsTick();
            }
            else
            {
                module.ticks = 0;
            }
        }
    }

}
