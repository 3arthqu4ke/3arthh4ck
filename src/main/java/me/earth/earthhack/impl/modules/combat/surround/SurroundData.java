package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyData;

final class SurroundData extends ObbyData<Surround>
{
    public SurroundData(Surround module)
    {
        super(module);
        register(module.center, "Moves you into the middle of " +
                "the block you are standing on.");
        register(module.movement, "-None well, nothing happens.\n" +
                "-Static if you move a block this module" +
                " will disable itself.\n-Limit If you move" +
                " faster than the Speed setting this module" +
                " won't do anything.\n-Disable if you move" +
                " faster than the Speed setting this module" +
                " will disable itself.");
        register(module.speed, "The maximum speed in km/h." +
                " Required for Movement modes Limit and Disable.");
        register(module.noTrap, "Always places blocks underneath your" +
                " surround to prevent people from stepping" +
                " into your surround from underneath.");
        register(module.floor, "Places a block underneath you.");
        register(module.extend, "Extends the surround to include" +
                " you when you stand in the middle of 2 or 4 blocks.");
        register(module.eDelay, "After you enable this module" +
                " it will wait for this time in milliseconds until it" +
                " fully extends. You have this time to move into" +
                " the middle of one block.");
        register(module.holeC, "Will center you even if you" +
                " are already in a 1x1 hole.");
        register(module.instant, "Analyzes packets to" +
                " surround as early as possible.");
        register(module.sound, "Analyzes Sound packets.");
        register(module.playerExtend,
                "Extends around players that block your surround. If you want" +
                " to use it I'd recommend a value of 1, higher values will" +
                " rarely come into play.");
        register(module.peNoTrap, "NoTrap for blocks placed by PlayerExtend. " +
                "Not required probably.");
        register(module.newVer, "Takes 1.13+ Mechanics into account" +
                " for some calculations.");
        register(module.deltaY,
                "Takes the players vertical speed into Account.");
        register(module.centerY, "Prevents you from getting centered down.");
        register(module.burrow, "Automatically enable BlockLag.");

    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Surrounds you legs with Obsidian.";
    }
}

