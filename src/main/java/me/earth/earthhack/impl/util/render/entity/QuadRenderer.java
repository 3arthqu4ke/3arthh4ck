package me.earth.earthhack.impl.util.render.entity;

import me.earth.earthhack.impl.util.math.Vector3f;

/**
 * For rendering a single face, perhaps of a cube.
 * @author megyn
 */
public abstract class QuadRenderer implements IRenderable
{

    private Vector3f[] vectors = new Vector3f[4];

    public QuadRenderer(Vector3f vec, Vector3f vec1, Vector3f vec2, Vector3f vec3)
    {
        vectors[0] = vec;
        vectors[1] = vec1;
        vectors[2] = vec2;
        vectors[3] = vec3;
    }

}
