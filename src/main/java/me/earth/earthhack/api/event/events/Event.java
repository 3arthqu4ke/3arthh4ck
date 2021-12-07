package me.earth.earthhack.api.event.events;

import me.earth.earthhack.api.event.bus.api.ICancellable;

public class Event implements ICancellable
{
    /** If this event is cancelled or not. */
    private boolean cancelled;

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

}

