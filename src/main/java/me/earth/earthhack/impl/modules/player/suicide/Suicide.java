package me.earth.earthhack.impl.modules.player.suicide;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.util.client.SettingUtil;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Objects;

public class Suicide extends DisablingModule {
    protected final Setting<SuicideMode> mode =
        register(new EnumSetting<>("Mode", SuicideMode.Command));
    private final Setting<Boolean> ask =
        register(new BooleanSetting("Ask", true));
    protected final Setting<Boolean> armor =
        register(new BooleanSetting("ThrowAwayArmor", true));
    protected final Setting<Boolean> turnOffOffhand =
        register(new BooleanSetting("TurnOffOffhand", true));
    protected final Setting<Boolean> throwAwayTotem =
        register(new BooleanSetting("ThrowAwayTotem", true));
    protected final Setting<Integer> throwDelay =
        register(new NumberSetting<>("Throw-Delay", 500, 0, 1000));
    private final Setting<Boolean> syncWithAc =
        register(new BooleanSetting("SynchronizeWithAC", false));
    protected final SuicideAutoCrystal autoCrystal =
        new SuicideAutoCrystal();
    protected final StopWatch timer = new StopWatch();
    protected boolean displaying;

    public Suicide() {
        super("Suicide", Category.Player);
        this.listeners.add(new ListenerMotion(this));
        boolean found = false;
        for (Setting<?> setting : autoCrystal.getSettings()) {
            if (setting.getName().equals("Page")) {
                found = true;
            }

            if (found) {
                this.register(setting);
            }
        }

        found = false;
        for (Setting<?> setting : this.getSettings()) {
            if (found) {
                Visibilities.VISIBILITY_MANAGER.registerVisibility(
                    setting, Visibilities.andComposer(
                        () -> mode.getValue() == SuicideMode.AutoCrystal));
            }

            if (setting == ask) {
                found = true;
            }
        }

        mode.addObserver(e -> this.disable());
        syncWithAc.addObserver(e -> {
            if (e.getValue()) {
                AutoCrystal crystal = Managers.MODULES.getByClass(
                    AutoCrystal.class);
                if (crystal != null) {
                    boolean foundPage = false;
                    for (Setting<?> setting : crystal.getSettings()) {
                        if (setting.getName().equals("Page")) {
                            foundPage = true;
                        }

                        if (foundPage) {
                            Setting<?> thisSetting = this.getSetting(
                                setting.getName());
                            if (thisSetting != null && Objects.equals(
                                thisSetting.getInitial(),
                                setting.getInitial())) {
                                SettingUtil.setUnchecked(thisSetting,
                                                         setting.getValue());
                            }
                        }
                    }
                }

                mc.addScheduledTask(() -> Scheduler.getInstance().schedule(
                    () -> syncWithAc.setValue(false)));
            }
        });
    }

    @Override
    protected void onEnable() {
        // TODO: make work on PingBypass server
        if (ask.getValue() && !PingBypass.isServer()) {
            displaying = true;
            GuiScreen current = mc.currentScreen;
            mc.displayGuiScreen(new YesNoNonPausing((r, id) -> {
                mc.displayGuiScreen(current);
                if (r) {
                    displaying = false;
                } else {
                    this.disable();
                }
            },
            TextColor.RED + "Do you want to kill yourself? (recommended)",
            "If you don't want to get asked again," +
                " turn off the \"Ask\" Setting.",
            1337));
            return;
        }

        displaying = false;
        if (mode.getValue() == SuicideMode.Command) {
            NetworkUtil.sendPacketNoEvent(new CPacketChatMessage("/kill"));
            this.disable();
        }
    }

    @Override
    protected void onDisable() {
        autoCrystal.disable();
    }

    public boolean shouldTakeOffArmor() {
        return this.isEnabled()
            && !displaying
            && mode.getValue() != SuicideMode.Command
            && armor.getValue();
    }

    public boolean deactivateOffhand() {
        return this.isEnabled()
            && !displaying
            && mode.getValue() != SuicideMode.Command
            && turnOffOffhand.getValue();
    }

}
