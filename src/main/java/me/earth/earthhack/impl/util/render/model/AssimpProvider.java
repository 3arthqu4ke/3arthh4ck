package me.earth.earthhack.impl.util.render.model;

import jassimp.AiWrapperProvider;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class AssimpProvider implements AiWrapperProvider<Vector3f, Matrix4f, Color, Object, Quaternion> {

    @Override
    public Vector3f wrapVector3f(ByteBuffer byteBuffer, int offset, int numComponents) {
        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        return new Vector3f(numComponents > 0 ? buffer.get(offset) : 0.0f, numComponents > 1 ? buffer.get(offset + 1) : 0.0f, numComponents > 2 ? buffer.get(offset + 2) : 0.0f);
    }

    @Override
    public Matrix4f wrapMatrix4f(float[] floats)
    {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        for (float aFloat : floats) {
            buffer.put(aFloat);
        }
        buffer.flip();
        return (Matrix4f) new Matrix4f().loadTranspose(buffer);
    }

    @Override
    public Color wrapColor(ByteBuffer byteBuffer, int i)
    {
        return new Color(byteBuffer.get(i), byteBuffer.get(i + 1), byteBuffer.get(i + 2), byteBuffer.get(i + 3));
    }

    @Override
    public Object wrapSceneNode(Object o, Object o1, int[] ints, String s)
    {
        return null;
    }

    @Override
    public Quaternion wrapQuaternion(ByteBuffer byteBuffer, int i)
    {
        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        return new Quaternion(buffer.get(i), buffer.get(i + 1), buffer.get(i + 2), buffer.get(i + 3));
    }

}
