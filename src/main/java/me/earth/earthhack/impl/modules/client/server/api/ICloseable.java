package me.earth.earthhack.impl.modules.client.server.api;

/**
 * {@link java.io.Closeable} without Exceptions.
 */
public interface ICloseable
{
    /**
     * Closes this without throwing an exception.
     */
    void close();

    boolean isOpen();

}
