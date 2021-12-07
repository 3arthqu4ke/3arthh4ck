package me.earth.earthhack.impl.util.misc.io;

import java.io.IOException;

/**
 * A {@link java.util.function.Consumer} that can throw
 * an {@link IOException}.
 *
 * @param <T> the type of the input to the operation
 *
 * @see BiIOConsumer
 */
@FunctionalInterface
public interface IOConsumer<T>
{
    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws IOException;

}
