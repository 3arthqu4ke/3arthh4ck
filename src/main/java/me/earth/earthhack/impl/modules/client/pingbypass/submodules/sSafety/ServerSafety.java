package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sSafety;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassSubmodule;
import me.earth.earthhack.impl.modules.client.safety.util.Update;

public class ServerSafety extends PingBypassSubmodule
{
    public ServerSafety(PingBypassModule pingBypass)
    {
        super(pingBypass, "S-Safety", Category.Client);
        register(new NumberSetting<>("MaxDamage", 4.0f, 0.0f, 36.0f));
        register(new BooleanSetting("BedCheck", false));
        register(new BooleanSetting("1.13+", false));
        register(new BooleanSetting("SafetyPlayer", false));
        register(new EnumSetting<>("Updates", Update.Tick));
        register(new NumberSetting<>("Delay", 25, 0, 100));
        this.setData(new ServerSafetyData(this));
    }

}
