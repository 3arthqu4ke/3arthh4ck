package me.earth.earthhack.api.cache;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Caches an Object that can be supplied by
 * the given {@link Supplier} or by using
 * {@link Cache#set(Object)}. To retrieve
 * the Object call the {@link Cache#get()}
 * method. If the object is not present the
 * {@link Supplier#get()} will be called.
 * There's no guarantee for the Object to
 * not be null still, but you can always
 * check by using {@link Cache#isPresent()}.
 *
 * Caches are not ThreadSafe!
 * Setting the cached value to null can cause
 * a NullPointer on a different thread.
 *
 * @param <T> The Type of Object to cache.
 */
public class Cache<T>
{
    /** The getter that gets the Object. */
    protected Supplier<T> getter;
    /** If the getter shouldn't be called. */
    protected boolean frozen;
    /** The Cached Object. */
    protected T cached;

    /** Constructs a new Cache with a getter that returns null. */
    protected Cache()
    {
        this.getter = () -> null;
    }

    /**
     * Constructs a Cache that already has its value present.
     *
     * @param value the cached object.
     */
    public Cache(T value)
    {
        this.getter = () -> value;
        this.cached = value;
    }

    /**
     * Constructs a new Cache.
     *
     * @param getter supplies the cached object.
     */
    public Cache(Supplier<T> getter)
    {
        this.getter = getter;
    }

    /**
     * Allows you to manually set the value of this cache.
     *
     * @param value the value that should be returned by {@link Cache#get()}.
     * @return this.
     */
    public Cache<T> set(T value)
    {
        this.cached = value;
        return this;
    }

    /**
     * Will try to return the cached Object. If the Object
     * is not present and this Cache is not frozen, the Supplier
     * will be called. {@link Cache#isPresent()} is called to achieve
     * the later behaviour.
     *
     * @return the cached object, or <tt>null</tt> if it's not present.
     */
    public T get()
    {
        if (isPresent())
        {
            return cached;
        }

        return null;
    }

    /**
     * If the Object is not present the supplier will be called first
     * (If this Cache is not frozen). If the Object is still <tt>null</tt>
     * afterwards, this method returns <tt>false</tt>.
     *
     * @return <tt>true</tt> if the cached Object is != null.
     */
    public boolean isPresent()
    {
        if (cached == null && !frozen)
        {
            cached = getter.get();
        }

        return cached != null;
    }

    /**
     * Makes the given Consumer accept the cached Object
     * if its not present (will call {@link Cache#isPresent()}) and
     * in that case returns <tt>true</tt>.
     *
     * @param consumer the consumer the cached object will be applied to.
     * @return <tt>true</tt> if the object is present.
     */
    public boolean computeIfPresent(Consumer<T> consumer)
    {
        if (isPresent())
        {
            consumer.accept(cached);
            return true;
        }

        return false;
    }

    /**
     * Similar to {@link Cache#computeIfPresent(Consumer)}, applies the
     * Function to the cached Object if it's present. If the
     * Object is not present, the given Function won't be applied
     * and the given defaultValue will be returned.
     *
     * @param function the function to apply.
     * @param defaultValue returned when the cached object is null.
     * @param <E> the Type of the return value.
     * @return the functions return value or the defaultValue. (@see above)
     */
    public <E> E returnIfPresent(Function<T, E> function, E defaultValue)
    {
        if (isPresent())
        {
            return function.apply(cached);
        }

        return defaultValue;
    }

    /**
     * If a cache is "frozen" (<tt>frozen == true</tt>), its
     * supplier won't be called. This can prevent memory leaks
     * when the supplier is costly and/or called often, but
     * just can't supply a value that isn't <tt>null</tt>.
     *
     * @param frozen if you want to un/freeze this Cache.
     */
    public void setFrozen(boolean frozen)
    {
        this.frozen = frozen;
    }

}
