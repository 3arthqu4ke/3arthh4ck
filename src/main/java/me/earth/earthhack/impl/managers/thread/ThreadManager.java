package me.earth.earthhack.impl.managers.thread;

import me.earth.earthhack.impl.util.thread.SafeRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@SuppressWarnings("UnusedReturnValue")
public class ThreadManager implements GlobalExecutor
{
    /**
     * It is always recommended to use this method over
     * {@link ThreadManager#submitRunnable(Runnable)}, unless you
     * are 100% sure your code can't and won't throw exceptions.
     * Because if it does you will break all Multithreading in
     * the client.
     *
     * Submits a {@link SafeRunnable} via
     * {@link ThreadManager#submitRunnable(Runnable)}.
     * Pure convenience so you can create SafeRunnable
     * Lambdas.
     *
     * @param runnable the runnable to submit.
     * @return the Future returned by {@link ExecutorService#submit(Runnable)}.
     */
    public Future<?> submit(SafeRunnable runnable)
    {
        return submitRunnable(runnable);
    }

    /**
     * Submits the given Runnable to an {@link ExecutorService}.
     * The Future that results from the call is returned.
     *
     * @param runnable the runnable to run on a separate thread.
     * @return the Future returned by the ExecutorService.
     */
    public Future<?> submitRunnable(Runnable runnable)
    {
        return EXECUTOR.submit(runnable);
    }

    /**
     * Shuts down {@link GlobalExecutor#EXECUTOR}.
     */
    public void shutDown()
    {
        EXECUTOR.shutdown();
    }

}
