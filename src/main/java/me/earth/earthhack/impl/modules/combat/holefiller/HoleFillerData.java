package me.earth.earthhack.impl.modules.combat.holefiller;

import me.earth.earthhack.impl.util.helpers.blocks.data.BlockPlacingData;

final class HoleFillerData extends BlockPlacingData<HoleFiller>
{
    public HoleFillerData(HoleFiller module)
    {
        super(module);
        register(module.range, "Holes will be filled within this range.");
        register(module.disable, "Automatically disables this" +
                " module after it ran for the given time " +
                "in milliseconds. If the delay is 0 " +
                "the Module won't turn off automatically.");
        register(module.longHoles, "If 2x1 holes should be filled.");
        register(module.bigHoles, "If 2x2 holes should be filled.");
        register(module.calcDelay,
                "For weak CPUs, calculates Holes less frequently.");
        register(module.requireTarget,
                "HoleFiller is only active when a player is in range.");
        register(module.targetRange,
                "Only players within this range will be targeted.");
        register(module.targetDistance,
                "Minimum Distance from closest player to block.");
        register(module.minSelf, "If you are not in a hole, holes that are" +
                " closer to you then this value will not be filled.");
        register(module.safety,
                "Takes Safety into account when using MaxSelf.");
        register(module.waitForHoleLeave,
                "Waits until the closest target leaves its hole.");
        register(module.offhand, "Uses the offhand for obsidian.");
        register(module.requireOffhand, "When offhand is on and you are not" +
            " holding obsidian the module will not do anything.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Fills holes around you.";
    }

}

