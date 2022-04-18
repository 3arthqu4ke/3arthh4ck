package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.api.module.data.DefaultData;

final class LongJumpData extends DefaultData<LongJump>
{
    public LongJumpData(LongJump module)
    {
        super(module);
        register(module.mode, "-Normal best for Anarchy\n-Cowabunga ...");
        register(module.boost, "Amount your jump will be boosted by.");
        register(module.noKick, "Prevents you from getting kicked by" +
                " disabling this module automatically.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Makes you jump far.";
    }

}
