package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.api.module.data.DefaultData;

final class FastSwimData extends DefaultData<FastSwim>
{
    public FastSwimData(FastSwim module)
    {
        super(module);
        register(module.vWater,
                "Multiplier for moving vertically through water.");
        register(module.hWater,
                "Multiplier for moving horizontally through water.");
        register(module.vLava,
                "Multiplier for moving vertically through lava.");
        register(module.hLava,
                "Multiplier for moving horizontally through lava.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Swim faster.";
    }

}
