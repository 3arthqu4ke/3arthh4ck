package me.earth.earthhack.impl.managers.minecraft;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

//TODO: prioritize windowClicks etc.
public class ClickManager
{
    private final Map<Integer, Queue<Runnable>> clicks = new TreeMap<>();

    public void scheduleClick(Runnable runnable, int priority)
    {

    }

    public void scheduleClickSynchronized(Runnable runnable, int priority)
    {
        synchronized (clicks)
        {
            scheduleClick(runnable, priority);
        }
    }

    public void runClick()
    {

    }

}
