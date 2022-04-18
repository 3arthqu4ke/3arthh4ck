package me.earth.earthhack.impl.modules.render.xray;

import me.earth.earthhack.api.module.data.DefaultData;

final class XRayData extends DefaultData<XRay>
{
    public XRayData(XRay module)
    {
        super(module);
        register(module.mode, "Simple is just Opacity with" +
                " an Opacity value of 0.");
        register(module.soft, "Makes the world not flicker when the " +
                "module is toggled or blocks are added.");
        register(module.opacity, "The Opacity value for Mode-Opacity.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Allows you to see through blocks.";
    }

}
