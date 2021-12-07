package me.earth.earthhack.api.event.bus.api;

/**
 * Handles events.
 *
 * @param <T>
 */
public interface Invoker<T>
{
    /**
     * Called when an event is posted on the bus.
     *
     * @param event the event.
     */
    void invoke(T event);

}
