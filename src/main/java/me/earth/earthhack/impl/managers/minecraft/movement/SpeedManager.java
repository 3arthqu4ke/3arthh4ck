package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.util.math.Vec3d;

public class SpeedManager extends SubscriberImpl implements Globals
{
    private final StopWatch timer = new StopWatch();
    private Vec3d last = new Vec3d(0, 0, 0);
    private double speed = 0.0f;

    public SpeedManager()
    {
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent event)
            {
                if (event.isSafe() && timer.passed(40))
                {
                    speed = MathUtil
                             .distance2D(mc.player.getPositionVector(), last);
                    last = mc.player.getPositionVector();
                    timer.reset();
                }
            }
        });
    }

    public double getSpeed()
    {
        return getSpeedBpS() * 3.6;
    }

    public double getSpeedBpS()
    {
        return speed * 20;
    }

}
