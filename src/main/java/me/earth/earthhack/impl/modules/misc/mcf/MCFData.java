package me.earth.earthhack.impl.modules.misc.mcf;

import me.earth.earthhack.api.module.data.DefaultData;

final class MCFData extends DefaultData<MCF>
{
    public MCFData(MCF mcf)
    {
        super(mcf);
    }

    @Override
    public int getColor()
    {
        return 0xff93FFA8;
    }

    @Override
    public String getDescription()
    {
        return "Middleclick on players to friend/unfriend them.";
    }

}
