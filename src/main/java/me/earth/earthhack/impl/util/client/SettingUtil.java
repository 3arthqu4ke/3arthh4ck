package me.earth.earthhack.impl.util.client;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.pingbypass.PingBypass;

import java.util.function.Consumer;

public class SettingUtil {
    @SuppressWarnings("unchecked")
    public static <T> void setUnchecked(Setting<T> setting, Object object) {
        setting.setValue((T) object);
    }

    public static void iterateAllSettings(Consumer<Setting<?>> action) {
        iterateAllSettings(Managers.MODULES, action);
        iterateAllSettings(PingBypass.MODULES, action);
    }

    public static void iterateAllSettings(Iterable<Module> modules,
                                   Consumer<Setting<?>> action) {
        for (Module module : modules) {
            for (Setting<?> setting : module.getSettings()) {
                action.accept(setting);
            }
        }
    }

}
