package me.earth.earthhack.impl.util.render.model.animation;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

// Again, this isn't C++, recode ASAP!
public class AssimpNodeData
{

    private Matrix4f transformation;
    private String name;
    private List<AssimpNodeData> children;

    public AssimpNodeData(String name)
    {
        this();
        this.name = name;
    }

    public AssimpNodeData()
    {
        this.children = new ArrayList<>();
        this.transformation = new Matrix4f();
    }

    public Matrix4f getTransformation()
    {
        return transformation;
    }

    public void setTransformation(Matrix4f transformation)
    {
        this.transformation = transformation;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<AssimpNodeData> getChildren()
    {
        return children;
    }

    public void setChildren(List<AssimpNodeData> children)
    {
        this.children = children;
    }

}
