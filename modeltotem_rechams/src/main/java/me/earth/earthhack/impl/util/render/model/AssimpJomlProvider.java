package me.earth.earthhack.impl.util.render.model;

import jassimp.AiWrapperProvider;
import me.earth.earthhack.impl.util.render.model.animation.SourceNode;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.util.Color;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class AssimpJomlProvider implements AiWrapperProvider<Vector3f, Matrix4f, Color, SourceNode, Quaternionf>
{

    @Override
    public Vector3f wrapVector3f(ByteBuffer byteBuffer, int i, int i1)
    {
        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        return new Vector3f(i1 > 0 ? buffer.get(i) : 0.0f, i1 > 1 ? buffer.get(i + 1) : 0.0f, i1 > 2 ? buffer.get(i + 2) : 0.0f);
    }

    @Override
    public Matrix4f wrapMatrix4f(float[] floats)
    {
        return new Matrix4f(
                floats[0], floats[1], floats[2], floats[3],
                floats[4], floats[5], floats[6], floats[7],
                floats[8], floats[9], floats[10], floats[11],
                floats[12], floats[13], floats[14], floats[15]
        );
    }

    @Override
    public Color wrapColor(ByteBuffer byteBuffer, int i)
    {
        return new Color(byteBuffer.get(i), byteBuffer.get(i + 1), byteBuffer.get(i + 2), byteBuffer.get(i + 3));
    }

    @Override
    public SourceNode wrapSceneNode(Object o, Object o1, int[] ints, String s)
    {
        Matrix4f matrix = (Matrix4f) o1;
        return new SourceNode(o == null ? null : (SourceNode) o, s, ints, matrix);
    }

    @Override
    public Quaternionf wrapQuaternion(ByteBuffer byteBuffer, int i)
    {
        try {
            FloatBuffer buffer = byteBuffer.asFloatBuffer();
            int limit = buffer.limit();
            float f1 = i > limit ? buffer.get(i) : 0.0f;
            // System.out.println("f1: " + f1);
            float f2 = i + 1 > limit ? buffer.get(i + 1) : 0.0f;
            // System.out.println("f2: " + f2);
            float f3 = i + 2 > limit ? buffer.get(i + 2) : 0.0f;
            // System.out.println("f3: " + f3);
            float f4 = i + 3 > limit ? buffer.get(i + 3) : 0.0f;
            // System.out.println("f4: " + f4);
            return new Quaternionf(f1, f2, f3, f4);
        } catch (IndexOutOfBoundsException ignored) {} // why does this happen???????
        return new Quaternionf();
    }

}
