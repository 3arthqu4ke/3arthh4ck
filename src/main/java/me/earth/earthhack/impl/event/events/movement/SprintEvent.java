package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Event;

public class SprintEvent extends Event
{
    private boolean sprinting;

    public SprintEvent(boolean sprinting)
    {
        this.sprinting = sprinting;
    }

    public void setSprinting(boolean sprinting)
    {
        this.sprinting = sprinting;
    }

    public boolean isSprinting()
    {
        return sprinting;
    }

}
