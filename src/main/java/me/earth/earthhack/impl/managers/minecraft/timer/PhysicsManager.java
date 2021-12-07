package me.earth.earthhack.impl.managers.minecraft.timer;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.PhysicsUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

/**
 * Manages Physics, a timer that doesn't run additional
 * ticks, but rather updates the player.
 */ //TODO: this
public class PhysicsManager extends SubscriberImpl implements Globals
{
    private final StopWatch timer = new StopWatch();
    private boolean blocking;
    private int delay;
    private int times;

    public PhysicsManager()
    {
        this.listeners.add(
            new EventListener<GameLoopEvent>(GameLoopEvent.class)
        {
            @Override
            public void invoke(GameLoopEvent event)
            {
                if (mc.player == null)
                {
                    times = 0;
                    return;
                }

                if (times > 0 && timer.passed(delay))
                {
                    blocking = true;
                    for (; times > 0; times--)
                    {
                        invokePhysics();
                        if (delay != 0)
                        {
                            break;
                        }
                    }

                    blocking = false;
                    timer.reset();
                }
            }
        });
        this.listeners.add(
            new EventListener<DisconnectEvent>
                (DisconnectEvent.class)
        {
            @Override
            public void invoke(DisconnectEvent event)
            {
                times = 0;
            }
        });
        this.listeners.add(
            new EventListener<WorldClientEvent.Load>
                (WorldClientEvent.Load.class)
        {
            @Override
            public void invoke(WorldClientEvent.Load event)
            {
                times = 0;
            }
        });
    }

    public void invokePhysics(int times, int delay)
    {
        if (!blocking)
        {
            this.times = times;
            this.delay = delay;
        }
    }

    /**
     * Invokes {@link Entity#onUpdate()} and
     * {@link EntityPlayerSP#onUpdateWalkingPlayer()} for
     * the player.
     */
    @SuppressWarnings("JavadocReference")
    public void invokePhysics()
    {
        PhysicsUtil.runPhysicsTick();
    }

}
