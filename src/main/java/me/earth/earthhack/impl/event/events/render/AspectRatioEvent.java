package me.earth.earthhack.impl.event.events.render;

public class AspectRatioEvent {
    private float aspectRatio;

    public AspectRatioEvent(float aspectRatio)
    {
        this.aspectRatio = aspectRatio;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
}