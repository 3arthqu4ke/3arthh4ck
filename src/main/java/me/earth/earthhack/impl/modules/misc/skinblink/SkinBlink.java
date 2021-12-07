package me.earth.earthhack.impl.modules.misc.skinblink;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.math.StopWatch;

public class SkinBlink extends Module
{
    protected final Setting<Integer> delay  =
            register(new NumberSetting<>("Delay", 1000, 0, 2000));
    protected final Setting<Boolean> random =
            register(new BooleanSetting("Random", false));

    protected final StopWatch timer = new StopWatch();

    public SkinBlink()
    {
        super("SkinBlink", Category.Misc);
        this.listeners.add(new ListenerGameLoop(this));
        this.setData(new SkinBlinkData(this));
    }

}
