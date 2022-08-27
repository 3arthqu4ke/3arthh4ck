package me.earth.earthhack.api.event.bus.api;

import java.util.Collection;

/**
 * A Subscriber containing listeners.
 */
public interface Subscriber
{
    /**
     * When {@link EventBus#subscribe(Object)} is called
     * all listeners contained in this list get subscribed
     * to the bus. The bus doesn't detect if listeners get
     * removed from or added to this collection while this
     * object is subscribed to it, in order to do that you
     * should resubscribe this Subscriber.
     *
     * @return the listeners to be subscribed on the bus.
     */
    Collection<Listener<?>> getListeners();

}
