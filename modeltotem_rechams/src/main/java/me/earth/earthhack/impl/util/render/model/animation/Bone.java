package me.earth.earthhack.impl.util.render.model.animation;

import jassimp.AiNodeAnim;
import me.earth.earthhack.impl.util.math.GlMathUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.model.AssimpJomlProvider;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

// TODO: dog code translated from c++, i HATE IT
public class Bone
{

    private final List<KeyPosition> positions = new ArrayList<>();
    private final List<KeyRotation> rotations = new ArrayList<>();
    private final List<KeyScale> scales = new ArrayList<>();
    private final String name;
    private final int id;

    private Matrix4f localTransformation = new Matrix4f();

    public Bone(String name, int id, AiNodeAnim node)
    {
        this.name = name;
        this.id = id;

        int numPositions = node.getNumPosKeys();
        for (int i = 0; i < numPositions; i++)
        {
            double x = node.getPosKeyX(i);
            double y = node.getPosKeyY(i);
            double z = node.getPosKeyZ(i);
            double timeStamp = node.getPosKeyTime(i);
            positions.add(new KeyPosition(new Vector3f((float) x, (float) y, (float) z), timeStamp));
        }

        int numRotations = node.getNumRotKeys();
        for (int i = 0; i < numRotations; i++)
        {
            Quaternionf x = node.getRotKeyQuaternion(i, new AssimpJomlProvider());
            double timeStamp = node.getRotKeyTime(i);
            rotations.add(new KeyRotation(x, timeStamp));
        }

        int numScales = node.getNumScaleKeys();
        for (int i = 0; i < numScales; i++)
        {
            double x = node.getScaleKeyX(i);
            double y = node.getScaleKeyY(i);
            double z = node.getScaleKeyZ(i);
            double timeStamp = node.getScaleKeyTime(i);
            scales.add(new KeyScale(new Vector3f((float) x, (float) y, (float) z), timeStamp));
        }
        System.out.println("Bone: " + name + " ID: " + id + " NumPositions: " + numPositions);
    }

    public void update(double partialTicks)
    {
        localTransformation = interpolatePos(partialTicks).mul(interpolateRotation(partialTicks), new Matrix4f()).mul(interpolateScale(partialTicks), new Matrix4f());
        // Matrix4f.mul(interpolatePos(partialTicks), interpolateRotation(partialTicks), localTransformation);
    }

    private float getScaleFactor(double lastTimeStamp, double nextTimeStamp, double animationTime)
    {
        float scaleFactor;
        double midWayLength = animationTime - lastTimeStamp;
        double framesDiff = nextTimeStamp - lastTimeStamp;
        scaleFactor = (float) (midWayLength / framesDiff);
        return scaleFactor;
    }

    private Matrix4f interpolatePos(double timestamp)
    {
        if (positions.size() == 1)
        {
            KeyPosition position = positions.get(0);
            return new Matrix4f().translate(position.getPosition());
        }
        int pos = getPosition(timestamp);
        int pos1 = pos + 1;
        float scaleFactor = getScaleFactor(positions.get(pos).getTimestamp(),
                positions.get(pos1).getTimestamp(), timestamp);
        Vector3f finalPosition = lerp(positions.get(pos).getPosition(),
                positions.get(pos1).getPosition(), scaleFactor);
        return GlMathUtil.initTranslationJoml(finalPosition);
    }

    private Matrix4f interpolateRotation(double timestamp)
    {
        if (rotations.size() == 1)
        {
            KeyRotation rotation = rotations.get(0);
            return rotation.getQuaternion().normalize().get(new Matrix4f());
        }
        int rot = getRotation(timestamp);
        int rot1 = rot + 1;
        float scaleFactor = getScaleFactor(rotations.get(rot).getTimestamp(),
                rotations.get(rot1).getTimestamp(), timestamp);
        return GlMathUtil.toRotationMatrixJoml(GlMathUtil.normalizedLerp(rotations.get(rot).getQuaternion(), rotations.get(rot1).getQuaternion(), scaleFactor));
        // return rotations.get(rot).getQuaternion().slerp(rotations.get(rot1).getQuaternion(), scaleFactor, new Quaternionf()).get(new Matrix4f());
    }

    private Matrix4f interpolateScale(double timestamp)
    {
        if (scales.size() == 1)
        {
            KeyScale scale = scales.get(0);
            return new Matrix4f().translate(scale.getScale());
        }
        int scale = getScale(timestamp);
        int scale1 = scale + 1;
        float scaleFactor = getScaleFactor(scales.get(scale).getTimestamp(),
                scales.get(scale1).getTimestamp(), timestamp);
        Vector3f finalScale = mix(scales.get(scale).getScale(), scales.get(scale1).getScale(), scaleFactor);
        return GlMathUtil.initScaleJoml(finalScale);
    }

    public int getPosition(double timestamp)
    {
        for (int i = 0; i < positions.size(); i++)
        {
            if (positions.get(i).getTimestamp() >= timestamp) return i;
        }
        return -1;
    }

    public int getRotation(double timestamp)
    {
        for (int i = 0; i < rotations.size(); i++)
        {
            if (rotations.get(i).getTimestamp() >= timestamp) return i;
        }
        return -1;
    }

    public int getScale(double timestamp)
    {
        for (int i = 0; i < scales.size(); i++)
        {
            if (scales.get(i).getTimestamp() >= timestamp) return i;
        }
        return -1;
    }

    public String getName()
    {
        return name;
    }

    public Matrix4f getLocalTransformation()
    {
        return localTransformation;
    }

    static class KeyPosition
    {
        private final Vector3f position;
        private final double timestamp;

        public KeyPosition(Vector3f position, double timestamp)
        {
            this.position = position;
            this.timestamp = timestamp;
        }

        public Vector3f getPosition()
        {
            return position;
        }

        public double getTimestamp()
        {
            return timestamp;
        }
    }

    static class KeyRotation
    {
        private final Quaternionf quaternion;
        private final double timestamp;

        public KeyRotation(Quaternionf quaternion, double timestamp)
        {
            this.quaternion = quaternion;
            this.timestamp = timestamp;
        }

        public Quaternionf getQuaternion()
        {
            return quaternion;
        }

        public double getTimestamp()
        {
            return timestamp;
        }
    }

    static class KeyScale
    {
        private final Vector3f scale;
        private final double timestamp;

        public KeyScale(Vector3f scale, double timestamp)
        {
            this.scale = scale;
            this.timestamp = timestamp;
        }

        public Vector3f getScale()
        {
            return scale;
        }

        public double getTimestamp()
        {
            return timestamp;
        }
    }

    public static Vector3f mix(
        Vector3f first, Vector3f second, float factor) {
        return new Vector3f(first.x * (1.0F - factor) + second.x * factor, first.y * (1.0F - factor) + second.y * factor, first.z * (1.0F - factor) + first.z * factor);
    }

    public static Vector3f lerp(
        Vector3f start, Vector3f end, float progression) {
        float x = start.x + (end.x - start.x) * progression;
        float y = start.y + (end.y - start.y) * progression;
        float z = start.z + (end.z - start.z) * progression;
        return new Vector3f(x, y, z);
    }

}
