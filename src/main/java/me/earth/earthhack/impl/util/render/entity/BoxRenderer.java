package me.earth.earthhack.impl.util.render.entity;

import me.earth.earthhack.impl.util.math.Vector3f;

public abstract class BoxRenderer
{

    private Vector3f min;
    private Vector3f max;

    public BoxRenderer(Vector3f min, Vector3f max)
    {
        this.min = min;
        this.max = max;
        setup();
    }

    public BoxRenderer(float x1, float y1, float z1, float x2, float z2, float y2)
    {
        this(new Vector3f(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2)), new Vector3f(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)));
    }

    abstract void render(float scale);

    abstract void setup();

    public Vector3f getMin()
    {
        return min;
    }

    public void setMin(Vector3f min)
    {
        this.min = min;
    }

    public Vector3f getMax()
    {
        return max;
    }

    public void setMax(Vector3f max)
    {
        this.max = max;
    }

}