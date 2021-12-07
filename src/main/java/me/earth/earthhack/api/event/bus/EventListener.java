package me.earth.earthhack.api.event.bus;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.event.bus.api.Listener;

/**
 * Implementation of the Listener interface.
 *
 * @param <T> type of object this listener listens to.
 */
public abstract class EventListener<T> implements Listener<T>
{
    private final Class<? super T> target;
    private final Class<?> type;
    private final int priority;

    public EventListener(Class<? super T> target)
    {
        this(target, EventBus.DEFAULT_PRIORITY, null);
    }

    public EventListener(Class<? super T> target, Class<?> type)
    {
        this(target, EventBus.DEFAULT_PRIORITY, type);
    }

    public EventListener(Class<? super T> target, int priority)
    {
        this(target, priority, null);
    }

    public EventListener(Class<? super T> target, int priority, Class<?> type)
    {
        this.priority = priority;
        this.target   = target;
        this.type     = type;
    }

    @Override
    public int getPriority()
    {
        return priority;
    }

    @Override
    public Class<? super T> getTarget()
    {
        return target;
    }

    @Override
    public Class<?> getType()
    {
        return type;
    }

}
