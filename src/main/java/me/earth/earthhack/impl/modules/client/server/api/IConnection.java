package me.earth.earthhack.impl.modules.client.server.api;

import me.earth.earthhack.api.util.interfaces.Nameable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface represents a connection.
 */
public interface IConnection extends IConnectionEntry, ICloseable, ISender
{
    /**
     * Sets the Name returned by {@link Nameable#getName()}
     * for this connection.
     *
     * @param name the new name.
     */
    void setName(String name);

    /**
     * @return the InputStream for this connection.
     */
    InputStream getInputStream() throws IOException;

    /**
     * @return this connections OutputStream.
     */
    OutputStream getOutputStream() throws IOException;

}
