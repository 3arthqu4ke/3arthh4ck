package me.earth.earthhack.impl.managers.minecraft.timer;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.core.mixins.util.ITimer;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.timer.Timer;

public class TimerManager extends SubscriberImpl implements Globals
{
    private static final ModuleCache<Timer> MODULE =
            Caches.getModule(Timer.class);

    private float speed;

    public TimerManager()
    {
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent event)
            {
                if (mc.player == null)
                {
                    reset();
                }
                else
                {
                    update();
                }
            }
        });
    }

    private void update()
    {
        if (MODULE.isEnabled())
        {
            ((ITimer) ((IMinecraft) mc).getTimer())
                    .setTickLength(50.0f / MODULE.get().getSpeed());
        }
        else
        {
            ((ITimer) ((IMinecraft) mc).getTimer())
                    .setTickLength(50.0f / speed);
        }
    }

    public void setTimer(float speed)
    {
        this.speed = speed <= 0.0f ? 0.1f : speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void reset()
    {
        this.speed = 1.0f;
    }

}
