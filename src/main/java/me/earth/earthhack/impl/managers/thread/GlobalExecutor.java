package me.earth.earthhack.impl.managers.thread;

import me.earth.earthhack.impl.util.thread.ThreadUtil;

import java.util.concurrent.ExecutorService;

/**
 * This class represents the {@link ExecutorService} 3arthh4ck uses.
 */
public interface GlobalExecutor
{
    /** The {@link ExecutorService} 3arthh4ck uses. */
    ExecutorService EXECUTOR = ThreadUtil.newDaemonCachedThreadPool();
    /** For tasks that can go out of Hand quickly. */
    ExecutorService FIXED_EXECUTOR = ThreadUtil.newFixedThreadPool(
            (int)(Runtime.getRuntime().availableProcessors() / 1.5));
}
