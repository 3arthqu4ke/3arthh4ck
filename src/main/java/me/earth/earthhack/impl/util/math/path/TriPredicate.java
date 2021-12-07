package me.earth.earthhack.impl.util.math.path;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * {@link Predicate}
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface TriPredicate<T, U, V>
{
    /**
     * {@link Predicate#test(Object)}
     */
    boolean test(T t, U u, V v);

    /**
     * {@link Predicate#and(Predicate)}
     */
    default TriPredicate<T, U, V> and(TriPredicate<? super T, ? super U, ? super V> other)
    {
        Objects.requireNonNull(other);
        return (T t, U u, V v) -> test(t, u, v) && other.test(t, u, v);
    }

    /**
     * {@link Predicate#negate()}
     */
    default TriPredicate<T, U, V> negate()
    {
        return (T t, U u, V v) -> !test(t, u, v);
    }

    /**
     * {@link Predicate#or(Predicate)}
     */
    default TriPredicate<T, U, V> or(TriPredicate<? super T, ? super U, ? super V> other)
    {
        Objects.requireNonNull(other);
        return (T t, U u, V v) -> test(t, u, v) || other.test(t, u, v);
    }
}
