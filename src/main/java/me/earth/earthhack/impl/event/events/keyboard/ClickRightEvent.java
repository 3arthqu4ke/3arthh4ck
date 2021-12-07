package me.earth.earthhack.impl.event.events.keyboard;

import me.earth.earthhack.api.event.events.Event;

public class ClickRightEvent extends Event
{
    private int delay;

    public ClickRightEvent(int delay)
    {
        this.delay = delay;
    }

    public int getDelay()
    {
        return delay;
    }

    public void setDelay(int delay)
    {
        this.delay = delay;
    }

}
