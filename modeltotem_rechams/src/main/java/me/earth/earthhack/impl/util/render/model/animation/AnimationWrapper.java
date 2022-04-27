package me.earth.earthhack.impl.util.render.model.animation;

import me.earth.earthhack.api.util.interfaces.Nameable;
import org.joml.Matrix4f;

import java.util.Map;

// handles setting bones in the model from the animation keyframes parsed from the animation class
public class AnimationWrapper implements Nameable
{

    private final Animation animation;
    private double currentTime;
    private double deltaTime;
    private Matrix4f[] matrices = new Matrix4f[100];

    public AnimationWrapper(Animation animation)
    {
        this.animation = animation;
        this.currentTime = 0.0;
        for (int i = 0; i < 100; i++)
        {
            matrices[i] = new Matrix4f(); // identity matrix
        }
    }

    public void update(double partialTicks)
    {
        deltaTime = partialTicks;
        currentTime += partialTicks * animation.getTPS();
        currentTime %= animation.getLength();
        calculateBoneTransformations(animation.getRoot(), new Matrix4f());
    }

    public void calculateBoneTransformations(AssimpNodeData node, Matrix4f parent)
    {
        String name = node.getName();
        Matrix4f transform = node.getTransformation();
        Bone bone = animation.findBone(name);

        if (bone != null)
        {
            bone.update(currentTime);
            transform = bone.getLocalTransformation();
        }

        Matrix4f globalTransform = transform.mul(parent);

        Map<String, BoneInfo> map = animation.getBoneInfoMap();
        if (!map.isEmpty()
                && map.containsKey(name))
        {
            int index = map.get(name).getId();
            Matrix4f off = map.get(name).getOffset();
            // if (index >= 100) return; // probably not good
            matrices[index] = off.mul(globalTransform);
        }

        for (AssimpNodeData child : node.getChildren())
        {
            calculateBoneTransformations(child, globalTransform);
        }
    }

    public Animation getAnimation()
    {
        return animation;
    }

    public Matrix4f[] getMatrices()
    {
        return matrices;
    }

    @Override
    public String getName() {
        return animation.getName();
    }

}
