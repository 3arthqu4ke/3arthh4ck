package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.api.module.data.DefaultData;

final class PhaseData extends DefaultData<Phase>
{
    public PhaseData(Phase module)
    {
        super(module);
        register(module.mode, "-Sand use doors/heads/sand to phase." +
                "\n-Climb goes down" +
                "\n-Packet uses packets to phase" +
                "\n-Normal just plain phase.");
        register(module.autoClip, "Tries to get you into " +
                "a block when you enable this module.");
        register(module.blocks, "Modifier for the AutoClip.");
        register(module.distance, "Modifier for the distance you want to " +
                "travel when using Phase-Mode-Normal.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Allows you to walk through blocks.";
    }

}
