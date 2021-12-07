package me.earth.earthhack.impl.util.render.model.animation;

import org.joml.Matrix4f;

public class BoneInfo
{

    private int id;
    private Matrix4f offset;

    public BoneInfo(int id, Matrix4f offset)
    {
        this.id = id;
        System.out.println("BoneInfo ID: " + id);
        this.offset = offset;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Matrix4f getOffset()
    {
        return offset;
    }

}
