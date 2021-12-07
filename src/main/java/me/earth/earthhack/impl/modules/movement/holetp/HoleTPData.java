package me.earth.earthhack.impl.modules.movement.holetp;

import me.earth.earthhack.api.module.data.DefaultData;

final class HoleTPData extends DefaultData<HoleTP>
{
    public HoleTPData(HoleTP module)
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
