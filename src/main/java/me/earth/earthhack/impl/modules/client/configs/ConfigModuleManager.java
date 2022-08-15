package me.earth.earthhack.impl.modules.client.configs;

import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.managers.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class ConfigModuleManager extends ModuleManager {
    private final List<Category> categories;

    public ConfigModuleManager(ConfigManager configManager) {
        this.categories = new ArrayList<>(configManager.getRegistered().size());
        for (ConfigHelper<?> helper : configManager.getRegistered()) {
            Category category = new Category(helper.getName(), 0);
            this.categories.add(category);
            for (Config config : helper.getConfigs()) {
                this.forceRegister(new ConfigHelperModule(
                    helper, config.getName(), category, this));
            }
        }
    }

    @Override
    public void init() {
        // NOP
    }

    public List<Category> getCategories() {
        return categories;
    }

}
