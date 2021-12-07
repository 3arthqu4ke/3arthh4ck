package me.earth.earthhack.impl.modules.misc.autorespawn;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;

public class AutoRespawn extends Module
{
    protected final Setting<Boolean> coords =
            register(new BooleanSetting("Coords", false));

    public AutoRespawn()
    {
        super("AutoRespawn", Category.Misc);
        this.listeners.add(new ListenerScreens(this));
        this.setData(new AutoRespawnData(this));
    }

}
