package me.earth.earthhack.api.observable;

/**
 * A generic. {@link java.util.Observer}
 *
 * @param <T> the type of value this Observer will be notified with.
 */
public interface Observer<T>
{
    /**
     * Should be called by the {@link Observable} this
     * Observer is registered in. Notifies this Observer
     * about a value change.
     *
     * @param value the value.
     */
    void onChange(T value);

}
