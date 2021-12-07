package me.earth.earthhack.impl.modules.movement.antimove;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.movement.antimove.modes.StaticMode;
import me.earth.earthhack.impl.util.client.SimpleData;

public class NoMove extends Module
{
    protected final Setting<StaticMode> mode =
            register(new EnumSetting<>("Mode", StaticMode.Stop));
    protected final Setting<Float> height    =
            register(new NumberSetting<>("Height", 4.0f, 0.0f, 256.0f));
    protected final Setting<Boolean> timer    =
            register(new BooleanSetting("Timer", false));

    public NoMove()
    {
        super("Static", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerUpdate(this));

        SimpleData data = new SimpleData(this,
                "Stops all Movement depending on the mode.");
        data.register(mode, "-Stop Stops all movement while this module" +
                " is enabled. Can be used to lag you back up when" +
                " you fall.\n-NoVoid stops all movement if there's" +
                " void underneath you.\n-Roof used to tp you up" +
                " 120 blocks on certain servers.");
        this.setData(data);
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().toString();
    }

}
