package me.earth.earthhack.impl.modules.misc.autorespawn;

import me.earth.earthhack.api.module.data.DefaultData;

final class AutoRespawnData extends DefaultData<AutoRespawn>
{
    public AutoRespawnData(AutoRespawn module)
    {
        super(module);
        register(module.coords,
                "Displays the coordinates of your death in chat.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Automatically respawns you after you died.";
    }

}
