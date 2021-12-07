package me.earth.earthhack.impl.modules.movement.reversestep;

import me.earth.earthhack.api.module.data.DefaultData;

final class ReverseStepData extends DefaultData<ReverseStep>
{
    public ReverseStepData(ReverseStep module)
    {
        super(module);
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "";
    }

}
