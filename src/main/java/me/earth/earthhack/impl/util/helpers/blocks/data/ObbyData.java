package me.earth.earthhack.impl.util.helpers.blocks.data;

import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;

public class ObbyData<T extends ObbyModule> extends BlockPlacingData<T>
{
    public ObbyData(T module)
    {
        super(module);
        register("Attack",
                "Attacks Crystals blocking your positions, "
                + "only recommended for Ping Players.");
        register("Pop",
                "- None : never pop when attacking\n" +
                "- Time : Risky, probably won't pop you\n" +
                "- Always : Safety over everything, Pops you to place blocks.");
        register("Cooldown",
                "Cooldown before attacking, see AutoCrystal - Cooldown");
        register("AntiWeakness", "Will perform a fast switch to a weakness " +
                "breaking item, when attacking and when cooldown is 0.");
        register("BreakDelay",
                "Delay to attack Crystals with.");
        register("Fast-Helping", "\n-Off will not cause illegitimate" +
                " rotations.\n-Down might cause illegitimate Rotation" +
                "s when placing downwards.\n-Fast might cause illegitimate" +
                " Rotations.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "A module that places blocks, specialized on Obby.";
    }

}
