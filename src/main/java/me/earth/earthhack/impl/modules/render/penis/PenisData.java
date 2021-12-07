package me.earth.earthhack.impl.modules.render.penis;

import me.earth.earthhack.api.module.data.DefaultData;

final class PenisData extends DefaultData<Penis>
{
    public PenisData(Penis module)
    {
        super(module);
        register(module.selfLength, "Self penis length.");
        register(module.friendLength, "Friend penis length.");
        register(module.enemyLength, "Enemy penis length.");
        register(module.uncircumcised, "Make pp uncircumcised");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Highlights Holes around you.";
    }

}
