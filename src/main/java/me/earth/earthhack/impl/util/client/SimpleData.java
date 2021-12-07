package me.earth.earthhack.impl.util.client;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.DefaultData;

public class SimpleData extends DefaultData<Module>
{
    private final int color;
    private final String description;

    public SimpleData(Module module, String description)
    {
        this(module, description, 0xffffffff);
    }

    public SimpleData(Module module, String description, int color)
    {
        super(module);
        this.color = color;
        this.description = description;
    }

    @Override
    public int getColor()
    {
        return color;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

}
