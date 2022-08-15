package me.earth.earthhack.pingbypass.modules;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.CommandSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.setting.RemovingSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.PingBypass;

import java.util.ConcurrentModificationException;

public class SyncModule extends Module {
    private final StopWatch timer = new StopWatch();

    public SyncModule() {
        super("Sync", Category.Client);
        this.setData(new SimpleData(
            this, "Syncs your clients modules with the modules you see here."));

        this.listeners.add(new LambdaListener<>(TickEvent.class, e -> {
            if (timer.passed(500)) {
                this.disable();
            }
        }));
    }

    @Override
    protected void onEnable() {
        timer.reset();
        ChatUtil.sendMessage("Syncing client modules with PingBypass...");
        for (Module module : Managers.MODULES.getRegistered()) {
            Module pbModule = PingBypass.MODULES.getObject(module.getName());
            if (pbModule == null) {
                continue;
            }

            try {
                sync(module, pbModule);
            } catch (ConcurrentModificationException e) {
                ModuleUtil.sendMessage(this,
                                       TextColor.DARK_RED + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static  <T> void setValue(Setting<T> setting, Object from) {
        setting.setValue((T) from);
    }

    public static void sync(Module module, Module pbModule) {
        for (Setting<?> pbSetting : pbModule.getSettings()) {
            Setting<?> moduleSetting = module.getSetting(pbSetting.getName());
            if (moduleSetting == null
                || pbSetting instanceof CommandSetting
                || pbSetting instanceof RemovingSetting) {
                continue;
            }

            try {
                setValue(pbSetting, moduleSetting.getValue());
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

}
