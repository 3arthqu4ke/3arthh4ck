package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.gui.click.HideableModule;
import me.earth.earthhack.impl.gui.module.impl.SimpleSubModule;

public class PingBypassSubmodule extends SimpleSubModule<PingBypassModule>
    implements HideableModule {
    public PingBypassSubmodule(PingBypassModule parent, String name, Category category) {
        super(parent, name, category);
    }

    @Override
    public boolean isModuleHidden() {
        return !getParent().isOld();
    }

}
