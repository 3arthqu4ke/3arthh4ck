package me.earth.earthhack.impl.modules.misc.tpssync;

import me.earth.earthhack.api.module.data.DefaultData;

final class TpsSyncData extends DefaultData<TpsSync>
{
    public TpsSyncData(TpsSync module)
    {
        super(module);
        register("Attack", "Syncs your attacks with the tps.");
        register("Mine", "Syncs mining blocks with the tps.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Syncs your actions with the servers tps.";
    }

}
