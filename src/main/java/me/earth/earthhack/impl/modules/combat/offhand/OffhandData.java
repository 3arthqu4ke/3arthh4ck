package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.api.module.data.DefaultData;

final class OffhandData extends DefaultData<Offhand>
{
    public OffhandData(Offhand module)
    {
        super(module);
        register(module.health, "If your health goes below" +
                " this threshold while you are not \"safe\"" +
                " (see SafetyManager) a totem will be switched" +
                " into your offhand.");
        register(module.safeH, "Same as Health but applies if you are safe.");
        register(module.gappleBind, "Click this Bind to" +
                " switch Gapples into your Offhand.");
        register(module.crystalBind, "Click this Bind to switch" +
                " Crystals into your Offhand.");
        register(module.delay, "Delay between 2 actions that move around" +
                " items in your inventory. Low delays can" +
                " cause desync while high delays will sometimes fail you.");
        register(module.cToTotem, "Switches to Totems into your Offhand" +
                " instead of Gapples if the GappleBind is pressed" +
                " while you are holding crystals in the offhand.");
        register(module.swordGap, "When holding rightclick while" +
                " you are holding a sword/axe in your mainhand" +
                " and a totem in your offhand Gapples will" +
                " be switched into your offhand.");
        register(module.recover, "Places back the item that was in the" +
                " offhand before a totem was put there for safety reasons.");
        register(module.noOGC, "While mainhanding crystals and offhanding" +
                " Gapples you might accidentally place crystals while" +
                " rightclicking to eat gapples. This setting prevents that.");
        register(module.hudMode, "Changes the way this module is displayed" +
                " in the HUD arraylist.");
        register(module.timeOut, "Delay for recovery.");
        register(module.crystalCheck,
                 "Checks for crystals dealing damage to you." +
                     " Should be redundant since Safety does this for you.");
        register(module.oldCrystalCheck, "The old bugged and thus way" +
            " too safe crystalCheck. Maybe someone likes it," +
            " so it has been left in.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "AutoTotem, OffhandCrystal, OffhandGapple, all in one.";
    }

}
