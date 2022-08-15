package me.earth.earthhack.impl.modules.client.configs;

import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.managers.config.helpers.CurrentConfig;
import me.earth.earthhack.impl.managers.config.util.ConfigDeleteException;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.io.IOException;

public class ConfigHelperModule extends Module {
    private final ConfigHelper<?> configHelper;
    private final ModuleManager moduleManager;
    private boolean runOnEnable = true;
    private boolean deleted;

    public ConfigHelperModule(ConfigHelper<?> configHelper, String name,
                              Category category,
                              ModuleManager moduleManager) {
        super(name, category);
        this.configHelper = configHelper;
        this.moduleManager = moduleManager;
        this.unregister(this.getSetting("Bind"));
        this.unregister(this.getSetting("Toggle"));
        this.unregister(this.getSetting("Hidden"));
        this.unregister(this.getSetting("Name"));

        String current = CurrentConfig.getInstance().get(configHelper);
        if (current != null && current.equalsIgnoreCase(name)) {
            runOnEnable = false;
            this.enable();
            runOnEnable = true;
        }

        this.register(new BooleanSetting("Save", false)).addObserver(e -> {
            e.setCancelled(true);
            try {
                configHelper.save(this.getName());
            } catch (IOException ex) {
                logMessage(ex.getMessage());
            }
        });

        this.register(new BooleanSetting("Refresh", false)).addObserver(e -> {
            e.setCancelled(true);
            try {
                configHelper.refresh(this.getName());
            } catch (IOException ex) {
                logMessage(ex.getMessage());
            }
        });

        this.register(new BooleanSetting("Delete", false)).addObserver(e -> {
            e.setCancelled(true);
            try {
                configHelper.delete(this.getName());
                deleted = true;
            } catch (IOException | ConfigDeleteException ex) {
                logMessage(ex.getMessage());
            }
        });
    }

    @Override
    protected void onEnable() {
        if (runOnEnable) {
            try {
                configHelper.load(this.getName());
                for (Module module : this.moduleManager.getModulesFromCategory(
                    this.getCategory())) {
                    module.disable();
                }
            } catch (IOException e) {
                logMessage(e.getMessage());
            }
        }
    }

    private void logMessage(String message) {
        ChatUtil.sendMessage("<" + getName() + "> " + TextColor.RED + message);
    }

    public boolean isDeleted() {
        return deleted;
    }

}
