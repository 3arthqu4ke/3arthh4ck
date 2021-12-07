package me.earth.earthhack.impl.util.misc.io;

import java.io.IOException;

/**
 * A {@link java.util.function.BiConsumer} that can throw
 * an {@link IOException}.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 *
 * @see IOConsumer
 */
@FunctionalInterface
public interface BiIOConsumer<T, U>
{
    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void accept(T t, U u) throws IOException;

}
