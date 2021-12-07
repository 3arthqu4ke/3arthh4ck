package me.earth.earthhack.impl.modules.misc.buildheight;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.client.SimpleData;

public class BuildHeight extends Module
{
    protected final Setting<Integer> height =
        register(new NumberSetting<>("Height", 255, 0, 420));
    protected final Setting<Boolean> crystals =
        register(new BooleanSetting("CrystalsOnly", false));

    public BuildHeight()
    {
        super("BuildHeight", Category.Misc);
        this.listeners.add(new ListenerPlaceBlock(this));
        this.setData(new SimpleData(this,
                "Allows you to place crystals at buildheight."));
    }

}
