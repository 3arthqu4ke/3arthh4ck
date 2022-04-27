package me.earth.earthhack.impl.util.render.model.animation;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class SourceNode
{

    private SourceNode parent;
    private String name;
    private int[] meshes;
    private Matrix4f transformation;
    private final List<SourceNode> children = new ArrayList<>();

    public SourceNode(SourceNode parent, String name, int[] meshes, Matrix4f transformation)
    {
        this.parent = parent;
        this.name = name;
        this.meshes = meshes;
        this.transformation = transformation;

        if (parent != null)
        {
            parent.addChild(this);
        }
    }

    public Matrix4f getTransformation()
    {
        return transformation;
    }

    public int[] getMeshes()
    {
        return meshes;
    }

    public String getName()
    {
        return name;
    }

    public SourceNode getParent()
    {
        return parent;
    }

    public void addChild(SourceNode child)
    {
        children.add(child);
    }

    public List<SourceNode> getChildren()
    {
        return children;
    }

}
