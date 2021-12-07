package me.earth.earthhack.impl.util.thread;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link com.google.common.util.concurrent.ThreadFactoryBuilder}.
 * So we are independent. Afaik the lib is licensed with Apache-2.0.
 */
@SuppressWarnings("UnusedReturnValue")
public class ThreadFactoryBuilder
{
    private Boolean daemon;
    private String nameFormat;

    public ThreadFactoryBuilder setDaemon(boolean daemon)
    {
        this.daemon = daemon;
        return this;
    }

    public ThreadFactoryBuilder setNameFormat(String nameFormat)
    {
        this.nameFormat = nameFormat;
        return this;
    }

    public ThreadFactory build()
    {
        Boolean daemon = this.daemon;
        String nameFormat = this.nameFormat;
        AtomicLong id = (nameFormat != null) ? new AtomicLong(0) : null;
        return r ->
        {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            if (daemon != null)
            {
                thread.setDaemon(daemon);
            }

            if (nameFormat != null)
            {
                thread.setName(format(nameFormat, id.getAndIncrement()));
            }

            return thread;
        };
    }

    private static String format(String format, Object... args)
    {
        return String.format(Locale.ROOT, format, args);
    }

}
