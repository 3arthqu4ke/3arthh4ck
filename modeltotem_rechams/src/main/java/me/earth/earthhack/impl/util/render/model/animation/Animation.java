package me.earth.earthhack.impl.util.render.model.animation;

import jassimp.AiAnimation;
import jassimp.AiNodeAnim;
import jassimp.AiScene;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.util.render.model.AssimpJomlProvider;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation implements Nameable
{

    private final String name;
    private double duration;
    private double ticksPerSecond;
    private AssimpNodeData root;

    private List<Bone> bones = new ArrayList<>();
    private Map<String, BoneInfo> boneInfoMap = new HashMap<>();

    public Animation(AiScene scene, RiggedModel model, int index)
    {
        AiAnimation animation = scene.getAnimations().get(index);
        this.name = animation.getName();
        System.out.println("Animation: " + name);
        duration = animation.getDuration();
        ticksPerSecond = animation.getTicksPerSecond();
        root = new AssimpNodeData();
        handleChildren(root, scene.getSceneRoot(new AssimpJomlProvider()));
        findMissingBones(animation, model);
    }

    private void handleChildren(AssimpNodeData data, SourceNode source)
    {
        data.setName(source.getName());
        data.setTransformation(source.getTransformation());

        for (SourceNode child : source.getChildren())
        {
            AssimpNodeData newData = new AssimpNodeData();
            handleChildren(newData, child);
            data.getChildren().add(newData);
        }
    }

    private void findMissingBones(AiAnimation animation, RiggedModel model)
    {
        int size = animation.getNumChannels();

        Map<String, BoneInfo> infoMap = model.getBones();
        int boneCount = model.getBoneCount();

        for (int i = 0; i < size; i++)
        {
            AiNodeAnim node = animation.getChannels().get(i);
            String name = node.getNodeName();

            if (!infoMap.containsKey(name))
            {
                infoMap.put(name, new BoneInfo(boneCount, new Matrix4f()));
                boneCount++;
                if (boneCount >= 100) break; // TODO
            }
            bones.add(new Bone(name, infoMap.get(name).getId(), node));
        }

        boneInfoMap = infoMap;
    }

    public Bone findBone(String name)
    {
        for (Bone bone : bones)
        {
            if (bone.getName().equalsIgnoreCase(name)) return bone;
        }
        return null;
    }

    public double getTPS()
    {
        return ticksPerSecond;
    }

    public double getLength()
    {
        return duration;
    }

    public AssimpNodeData getRoot()
    {
        return root;
    }

    public Map<String, BoneInfo> getBoneInfoMap()
    {
        return boneInfoMap;
    }

    @Override
    public String getName() {
        return name;
    }

}
