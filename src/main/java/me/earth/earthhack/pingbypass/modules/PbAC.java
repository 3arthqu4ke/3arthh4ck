package me.earth.earthhack.pingbypass.modules;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACPages;

public class PbAC {
    @SuppressWarnings("unchecked")
    public static void registerAcPages(Module module) {
        new PageBuilder<>(module, ((Setting<ACPages>) module.getSetting("Page")))
            .addPage(p -> p == ACPages.Place, module.getSetting("Place"), module.getSetting("Simulate-Place"))
            .addPage(p -> p == ACPages.Break, module.getSetting("Attack"), module.getSetting("BreakSwing"))
            .addPage(p -> p == ACPages.Rotate, module.getSetting("Rotate"), module.getSetting("Ping-Existed"))
            .addPage(p -> p == ACPages.Misc, module.getSetting("TargetRange"), module.getSetting("Motion-Calc"))
            .addPage(p -> p == ACPages.FacePlace, module.getSetting("HoldFacePlace"), module.getSetting("FallBackDmg"))
            .addPage(p -> p == ACPages.Switch, module.getSetting("AutoSwitch"), module.getSetting("ObbyHand"))
            .addPage(p -> p == ACPages.Render, module.getSetting("Render"), module.getSetting("DamageMode"))
            .addPage(p -> p == ACPages.SetDead, module.getSetting("SetDead"), module.getSetting("Death-Time"))
            .addPage(p -> p == ACPages.Obsidian, module.getSetting("Obsidian"), module.getSetting("Obby-Rotate"))
            .addPage(p -> p == ACPages.Liquids, module.getSetting("Interact"), module.getSetting("Sponges"))
            .addPage(p -> p == ACPages.AntiTotem, module.getSetting("AntiTotem"), module.getSetting("Attempts"))
            .addPage(p -> p == ACPages.DamageSync, module.getSetting("DamageSync"), module.getSetting("SurroundSync"))
            .addPage(p -> p == ACPages.Extrapolation, module.getSetting("Extrapolation"), module.getSetting("SelfExtrapolation"))
            .addPage(p -> p == ACPages.GodModule, module.getSetting("ID-Predict"), module.getSetting("God-Swing"))
            .addPage(p -> p == ACPages.MultiThread, module.getSetting("Pre-Calc"), module.getSetting("BlockChangeThread"))
            .addPage(p -> p == ACPages.Development, module.getSetting("Priority"), module.getSetting("Remove-Time"))
            .register(Visibilities.VISIBILITY_MANAGER);
    }

}
