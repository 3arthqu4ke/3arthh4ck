package me.earth.earthhack.impl.core.transfomer.patch;

import me.earth.earthhack.impl.core.transfomer.Patch;

public abstract class AbstractPatch implements Patch
{
    private final String name;
    private final String transformed;

    public AbstractPatch(String name, String transformed)
    {
        this.name = name;
        this.transformed = transformed;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getTransformedName()
    {
        return transformed;
    }

}
