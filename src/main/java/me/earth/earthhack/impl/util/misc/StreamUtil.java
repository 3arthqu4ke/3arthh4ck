package me.earth.earthhack.impl.util.misc;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class StreamUtil
{
    public static void copy(URL from, URL to) throws IOException
    {
        try (ReadableByteChannel rbc = Channels.newChannel(from.openStream());
             FileOutputStream fos    = new FileOutputStream(to.getFile()))
        {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    public static byte[] toByteArray(InputStream is)
            throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        copy(is, buffer);
        return buffer.toByteArray();
    }

    /**
     * Copies the given InputStream to the given OutputStream.
     *
     * @param is the InputStream.
     * @param os the OutputStream.
     * @throws IOException on is.read or os.write.
     */
    public static void copy(InputStream is, OutputStream os)
            throws IOException
    {
        int length;
        byte[] bytes = new byte[1024];
        while ((length = is.read(bytes)) != -1)
        {
            os.write(bytes, 0, length);
        }
    }

}
