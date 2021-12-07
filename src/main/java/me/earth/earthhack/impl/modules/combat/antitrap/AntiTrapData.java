package me.earth.earthhack.impl.modules.combat.antitrap;

import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyData;

final class AntiTrapData extends ObbyData<AntiTrap>
{
    public AntiTrapData(AntiTrap module)
    {
        super(module);
        register(module.mode, "-Crystal places a crystal so you can't " +
                "get trapped.\n-FacePlace like Surround but one block higher." +
                "\n-Fill fills spots where crystals" +
                " could be placed around you.");
        register(module.offhand, "Switches to the Offhand.");
        register(module.timeOut, "Interval between toggling this module." +
                " (for fat fingers)");
        register(module.empty, "For Mode-Crystal: Disable the module if no " +
                "suitable position can be found. Otherwise it will run until " +
                "it can place a crystal.");
        register(module.swing, "If you want to see your hand swinging or not.");
        register(module.highFill, "For Mode-Faceplace/Fill: If all feettrap" +
                " positions should be filled 2 blocks high.");
        register(module.confirm,
                "Time for the server to confirm Blockplacements.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Places a crystal next to you to block possible AutoTraps.";
    }
}
