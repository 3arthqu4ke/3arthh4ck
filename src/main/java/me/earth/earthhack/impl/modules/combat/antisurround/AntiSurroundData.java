package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyListenerData;

final class AntiSurroundData extends ObbyListenerData<AntiSurround>
{
    public AntiSurroundData(AntiSurround module)
    {
        super(module);
        register(module.normal, "Turning this off (and Async) allows you to" +
                " use this module for PreCrystal only.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Breaks enemies Surrounds.";
    }

}
