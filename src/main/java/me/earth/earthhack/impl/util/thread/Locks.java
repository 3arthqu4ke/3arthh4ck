package me.earth.earthhack.impl.util.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Locks that the client uses.
 */
public class Locks
{
    /** Locks when placing or switching the mainhand slot. */
    public static final Lock PLACE_SWITCH_LOCK = new ReentrantLock();
    // TODO: Use this everywhere!
    /** Locks when clicking Items in your Inventory. */
    public static final Lock WINDOW_CLICK_LOCK = new ReentrantLock();
    /** Locks the processing of new packets until PingBypass has sent all JoinWorldPackets. */
    public static final Lock PINGBYPASS_PACKET_LOCK = new ReentrantLock();

    /**
     * Locks the given Lock, runs the given Runnable and finally
     * unlocks the lock.
     *
     * @param lock the lock to lock.
     * @param runnable the runnable to run while the lock is locked.
     */
    public static void acquire(Lock lock, Runnable runnable)
    {
        try
        {
            lock.lock();
            runnable.run();
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * Wraps the Runnable into a {@link Locks#acquire(Lock, Runnable)}
     * call and returns the new wrapper.
     *
     * @param lock the lock to use in the acquire call.
     * @param runnable the runnable to wrap.
     * @return a Runnable that calls {@link Locks#acquire(Lock, Runnable)}.
     */
    public static Runnable wrap(Lock lock, Runnable runnable)
    {
        return () -> acquire(lock, runnable);
    }

}
