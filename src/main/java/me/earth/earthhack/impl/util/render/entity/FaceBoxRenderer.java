package me.earth.earthhack.impl.util.render.entity;

import me.earth.earthhack.impl.util.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class FaceBoxRenderer extends BoxRenderer
{

    private List<PlainQuad> quads = new ArrayList<>();

    public FaceBoxRenderer(Vector3f min, Vector3f max)
    {
        super(min, max);
    }

    @Override
    void render(float scale)
    {

    }

    @Override
    void setup()
    {

    }

}
