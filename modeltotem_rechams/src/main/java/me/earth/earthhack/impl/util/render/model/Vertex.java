package me.earth.earthhack.impl.util.render.model;

import me.earth.earthhack.impl.util.math.Vec2d;
import net.minecraft.util.math.Vec3d;

public class Vertex
{

    private final Vec3d position;
    private Vec3d normal; // unnecessary in the scope of minecraft when no proper lighting engine is present
    private Vec2d tex;

    private int[] bones;
    private float[] boneWeights;

    public Vertex(Vec3d position, Vec3d normal, Vec2d tex)
    {
        this.position = position;
        this.normal = normal;
        this.tex = tex;
        this.bones = new int[4];
        this.boneWeights = new float[4];
        for (int i = 0; i < 4; i++) {
            bones[i] = -1;
            boneWeights[i] = 0.0f;
        }
    }

    public Vec3d getPosition()
    {
        return position;
    }

    public Vec3d getNormal()
    {
        return normal;
    }

    public Vec2d getTex()
    {
        return tex;
    }

    public int[] getBones()
    {
        return bones;
    }

    public float[] getWeights()
    {
        return boneWeights;
    }

}
