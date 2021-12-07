package me.earth.earthhack.impl.modules.misc.antivanish;

import me.earth.earthhack.api.module.data.DefaultData;

final class AntiVanishData extends DefaultData<AntiVanish>
{
    public AntiVanishData(AntiVanish module)
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
        return "Detects players that go into vanish.";
    }

}

