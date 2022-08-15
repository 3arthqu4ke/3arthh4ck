package me.earth.earthhack.impl.modules.client.settings;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.util.client.SimpleData;

public class SettingsModule extends Module {
    public static final Setting<Complexity> COMPLEXITY =
        new EnumSetting<>("Complexity", Complexity.Beginner);

    public SettingsModule() {
        super("Settings", Category.Client);
        this.register(COMPLEXITY);
        SimpleData data = new SimpleData(this, "Configure how Settings work.");
        data.register(
            COMPLEXITY,
            "-Beginner: these settings can be understood by everyone!\n" +
            "-Medium: requires some knowledge of clients and CrystalPvP.\n" +
            "-Expert: possibly requires knowledge of the code base and can " +
                "cause serious issues when badly configured.");
        this.setData(data);
    }

    @Override
    public String getDisplayInfo() {
        return COMPLEXITY.getValue().toString();
    }

    public static boolean shouldDisplay(Setting<?> setting) {
        return COMPLEXITY.getValue().shouldDisplay(setting);
    }

}
