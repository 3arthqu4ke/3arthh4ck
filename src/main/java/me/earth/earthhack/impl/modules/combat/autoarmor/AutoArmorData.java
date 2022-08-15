package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.api.module.data.DefaultData;

final class AutoArmorData extends DefaultData<AutoArmor>
{
    public AutoArmorData(AutoArmor module)
    {
        super(module);
        register(module.delay, "Delay for for moving around items" +
                " in your inventory. Low values might cause inventory desync.");
        register(module.autoMend, "Takes armor off while you're mending and" +
                " the armor reaches a certain threshold.");
        register(module.helmet, "The automend threshold to take" +
                " the helmet off.");
        register(module.chest, "The automend threshold to take" +
                " the chestplate off.");
        register(module.legs, "The automend threshold to take" +
                " the leggings off.");
        register(module.boots, "The automend threshold to take" +
                " the boots off.");
        register(module.curse, "If you want to allow Curse" +
                " of Binding armor to be put on.");
        register(module.closest, "Automend calculates if its" +
                " safe to take of armor. If an enemy player is " +
                "closer than this value and can damage you armor" +
                " won't be taken off.");
        register(module.maxDmg, "If more damage than this can be" +
                " dealt to you armor won't be taken off.");
        register(module.newVer, "Takes 1.13+ mechanics into account" +
                " while calculating your safety.");
        register(module.bedCheck, "Takes beds into account while" +
                " calculating your safety.");
        register(module.noDesync, "Attempts to resync your inventory (BETA).");
        register(module.screenCheck,
                "Doesn't allow taking off armor while in a gui.");
        register(module.softInInv, "Will not put on Armor while you" +
            " are arranging your Inventory.");
        register(module.fast, "Does not work!");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Automatically puts Armor on.";
    }

}
