package me.earth.earthhack.impl.modules.misc.skinblink;

import me.earth.earthhack.api.module.data.DefaultData;

final class SkinBlinkData extends DefaultData<SkinBlink>
{
    public SkinBlinkData(SkinBlink module)
    {
        super(module);
        register(module.delay, "The interval in milliseconds" +
                " your skin will blink in.");
        register(module.random, "Randomized the delay.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Toggles the layers of your skin.";
    }

}
