package me.earth.earthhack.api.event.bus;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.event.bus.api.ICancellable;
import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.event.bus.api.Subscriber;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An EventBus implementation.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SimpleBus implements EventBus
{
    private final Map<Class<?>, List<Listener>> listeners;
    private final Set<Subscriber> subscribers;
    private final Set<Listener> subbedlisteners;

    public SimpleBus()
    {
        listeners = new ConcurrentHashMap<>();
        subscribers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        subbedlisteners = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public void post(Object object)
    {
        List<Listener> listening = listeners.get(object.getClass());
        if (listening != null)
        {
            for (Listener listener : listening)
            {
                listener.invoke(object);
            }
        }
    }

    @Override
    public void post(Object object, Class<?> type)
    {
        List<Listener> listening = listeners.get(object.getClass());
        if (listening != null)
        {
            for (Listener listener : listening)
            {
                if (listener.getType() == null || listener.getType() == type)
                {
                    listener.invoke(object);
                }
            }
        }
    }

    @Override
    public boolean postCancellable(ICancellable object)
    {
        List<Listener> listening = listeners.get(object.getClass());
        if (listening != null)
        {
            for (Listener listener : listening)
            {
                listener.invoke(object);
                if (object.isCancelled())
                {
                    return true;
                }
            }
        }

        return object.isCancelled();
    }

    @Override
    public boolean postCancellable(ICancellable object, Class<?> type)
    {
        List<Listener> listening = listeners.get(object.getClass());
        if (listening != null)
        {
            for (Listener listener : listening)
            {
                if (listener.getType() == null || listener.getType() == type)
                {
                    listener.invoke(object);
                    if (object.isCancelled())
                    {
                        return true;
                    }
                }
            }
        }

        return object.isCancelled();
    }

    @Override
    public void postReversed(Object object, Class<?> type)
    {
        List<Listener> list = listeners.get(object.getClass());
        if (list != null)
        {
            ListIterator<Listener> li = list.listIterator(list.size());
            while(li.hasPrevious())
            {
                Listener l = li.previous();
                if (l != null
                        && (l.getType() == null || l.getType() == type))
                {
                    l.invoke(object);
                }
            }
        }
    }

    @Override
    public void subscribe(Object object)
    {
        if (object instanceof Subscriber)
        {
            Subscriber subscriber = (Subscriber) object;
            for (Listener<?> listener : subscriber.getListeners())
            {
                register(listener);
            }

            subscribers.add(subscriber);
        }
    }

    @Override
    public void unsubscribe(Object object)
    {
        if (object instanceof Subscriber)
        {
            Subscriber subscriber = (Subscriber) object;
            for (Listener<?> listener : subscriber.getListeners())
            {
                unregister(listener);
            }

            subscribers.remove(subscriber);
        }
    }

    @Override
    public void register(Listener<?> listener)
    {
        if (subbedlisteners.add(listener))
        {
            addAtPriority(listener, listeners.computeIfAbsent(
                    listener.getTarget(), v -> new CopyOnWriteArrayList<>()));
        }
    }

    @Override
    public void unregister(Listener<?> listener)
    {
        if (subbedlisteners.remove(listener))
        {
            List<Listener> list = listeners.get(listener.getTarget());
            if (list != null)
            {
                list.remove(listener);
            }
        }
    }

    @Override
    public boolean isSubscribed(Object object)
    {
        if (object instanceof Subscriber)
        {
            return subscribers.contains(object);
        }
        else if (object instanceof Listener)
        {
            return subbedlisteners.contains(object);
        }

        return false;
    }

    @Override
    public boolean hasSubscribers(Class<?> clazz)
    {
        List<Listener> listening = listeners.get(clazz);
        return listening != null && !listening.isEmpty();
    }

    @Override
    public boolean hasSubscribers(Class<?> clazz, Class<?> type)
    {
        List<Listener> listening = listeners.get(clazz);
        return listening != null && listening.stream().anyMatch(listener ->
                listener.getType() == null || listener.getType() == type);
    }

    /**
     * Adds the given listener to the given list, at an index
     * so that every listener coming after that index has the
     * same priority as the given one, or lower.
     *
     * @param listener the listener to add.
     * @param list the list to add the listener to.
     */
    private void addAtPriority(Listener<?> listener, List<Listener> list)
    {
        int index = 0;
        while (index < list.size()
                && listener.getPriority() < list.get(index).getPriority())
        {
            index++;
        }

        list.add(index, listener);
    }

}
