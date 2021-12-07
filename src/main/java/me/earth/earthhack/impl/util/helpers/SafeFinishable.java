package me.earth.earthhack.impl.util.helpers;

import me.earth.earthhack.impl.util.thread.SafeRunnable;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SafeFinishable extends Finishable implements SafeRunnable
{
    public SafeFinishable()
    {
        this(new AtomicBoolean());
    }

    public SafeFinishable(AtomicBoolean finished)
    {
        super(finished);
    }

    @Override
    public void run()
    {
        try
        {
            runSafely();
        }
        catch (Throwable t)
        {
            handle(t);
        }
        finally
        {
            setFinished(true);
        }
    }

    /**
     * SafeFinishable uses {@link SafeRunnable#runSafely()} instead.
     */
    @Deprecated
    protected void execute()
    {
        // NOOP
    }

}
