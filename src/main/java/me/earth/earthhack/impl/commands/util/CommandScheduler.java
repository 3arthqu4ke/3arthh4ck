package me.earth.earthhack.impl.commands.util;

import me.earth.earthhack.impl.util.thread.ThreadUtil;

import java.util.concurrent.ScheduledExecutorService;

/**
 * ScheduledExecutorService for commands.
 */
public interface CommandScheduler
{
    /**
     * Only use for small short lived tasks, which are rarely called!
     */
    ScheduledExecutorService SCHEDULER =
            ThreadUtil.newDaemonScheduledExecutor("Command");
}
