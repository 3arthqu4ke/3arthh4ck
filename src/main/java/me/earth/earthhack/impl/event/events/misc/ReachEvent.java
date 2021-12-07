package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;

public class ReachEvent extends Event
{
    private float reach;
    private float hitBox;

    public ReachEvent(float reach, float hitBox)
    {
        this.reach  = reach;
        this.hitBox = hitBox;
    }

    public float getReach()
    {
        return reach;
    }

    public void setReach(float reach)
    {
        this.reach = reach;
    }

    public float getHitBox()
    {
        return hitBox;
    }

    public void setHitBox(float hitBox)
    {
        this.hitBox = hitBox;
    }

}
