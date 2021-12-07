package me.earth.earthhack.api.event.bus.api;

/**
 * A listener, used to invoke events.
 *
 * @param <T> the type of object this listener receives.
 */
public interface Listener<T> extends Invoker<T>
{
    /**
     * Returns this listeners priority.
     * {@link Invoker#invoke} gets called for every
     * listener in order of their priority, highest
     * first.
     *
     * @return this listeners priority.
     */
    int getPriority();

    /**
     * Returns the type of object this
     * Listener listens to. Note that the
     * <tt>? super T</tt> should be read as <tt>T</tt>.
     * Its like that because of type erasure
     * doesn't allow us to reference generic
     * classes.
     *
     * @return the type of event this listener listens to.
     */
    Class<? super T> getTarget();

    /**
     * Returns a class object if this listener listens
     * to a specific parametrized type of object.
     * For wildcards or if the target is unparametrized null.
     * Relevant when {@link EventBus#post(Object, Class)} is
     * called.
     *
     * @return the parametrized type this listener requires.
     */
    Class<?> getType();

}
