package me.earth.earthhack.impl.event.events.render;

public class Render3DEvent
{
    private final float partialTicks;

    public Render3DEvent(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

}
