package me.earth.earthhack.impl.modules.client.configs;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.client.SimpleData;

import java.awt.*;

public class ConfigModule extends ClickGui {
    public ConfigModule() {
        super("Config-Gui");
        this.setData(new SimpleData(this, "Gui for managing configs."));
        this.color.setValue(new Color(255, 0, 0));
    }

    @Override
    protected Click newClick() {
        ConfigModuleManager moduleManager =
            new ConfigModuleManager(Managers.CONFIG);
        Click click = new Click(screen, moduleManager);
        click.setAddDescriptionFrame(false);
        click.setCategories(
            moduleManager.getCategories().toArray(new Category[0]));
        return click;
    }

}
