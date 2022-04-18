package me.earth.earthhack.impl.modules.combat.autotrap;

import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyListenerData;

final class AutoTrapData extends ObbyListenerData<AutoTrap>
{
    public AutoTrapData(AutoTrap module)
    {
        super(module);
        register(module.range, "The Range blocks will be placed in." +
                " Almost no servers allow you to place Obsidian" +
                " further than 6 blocks away.");
        register(module.noScaffold, "Will place an extra block " +
                "above the enemies head, preventing him from breaking " +
                "the block above him and scaffolding up or stepping out " +
                "of the AutoTrap.");
        register(module.noStep, "Places additional blocks around " +
                "the block directly above the players head preventing " +
                "him from stepping out if he mines the block above him.");
        register(module.upperBody, "Surrounds the upper body of the" +
                " target with blocks. A reason to have this off would" +
                " be to have the trap open for the PistonAura.");
        register(module.legs, "Will trap the enemies legs.");
        register(module.platform, "Will build a platform" +
                " underneath the enemy.");
        register(module.noDrop, "Will place an additional block underneath" +
                " the enemies feet so he's still trapped if he mines down.");
        register(module.extend, "Will extend the Trap if the" +
                " enemy stands between 2 or even 4 blocks. " +
                "A value of 1 means no extension.");
        register(module.targetMode, "Closest will only trap the closest" +
                " player, while Untrapped will trap every player" +
                " in range that isn't trapped yet.");
        register(module.speed, "The max speed a player " +
                "can move with to still be trapped.");
        register(module.freeCam, "If AutoTrap should be active" +
                " while you are in Freecam.");
        register(module.helping, "If helping blocks should be placed " +
                "if the main trap blocks can't be placed.");
        register(module.confirm, "Time to confirm that a block has been" +
                " placed on the server as well. Depends on your ping.");
        register(module.upperFace, "Prevents FacePlace (BETA).");
        register(module.bigExtend, "Extend NoScaffold, NoStep etc. too.");
        register(module.smartTop,
                "Helping Block when using NoScaffold without Top.");
        register(module.noScaffoldPlus,
                "Additional Helping Block when using NoScaffold without Top.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Traps enemy players with Obsidian.";
    }

}
