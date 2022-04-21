package me.earth.earthhack.impl.modules.misc.antipotion;

import me.earth.earthhack.api.module.data.DefaultData;

final class AntiPotionData extends DefaultData<AntiPotion>
{
    public AntiPotionData(AntiPotion module)
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
        return "Allows you to remove potions client-sided." +
                " Only works for some potions like levitation.";
    }

}

