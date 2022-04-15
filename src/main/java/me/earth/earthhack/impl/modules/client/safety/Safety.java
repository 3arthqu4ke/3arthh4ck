package me.earth.earthhack.impl.modules.client.safety;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.safety.util.Update;
import me.earth.earthhack.impl.util.minecraft.ICachedDamage;
import me.earth.earthhack.impl.util.text.TextColor;

/**
 * {@link me.earth.earthhack.impl.managers.thread.safety.SafetyManager}
 */
public class Safety extends Module
{
    public Safety()
    {
        super("Safety", Category.Client);
        register(new NumberSetting<>("MaxDamage", 4.0f, 0.0f, 36.0f));
        register(new BooleanSetting("BedCheck", false));
        register(new BooleanSetting("1.13+", false));
        register(new BooleanSetting("1.13-Entities", false));
        register(new EnumSetting<>("Updates", Update.Tick));
        register(new NumberSetting<>("Delay", 25, 0, 100));
        register(new BooleanSetting("2x1s", true));
        register(new BooleanSetting("2x2s", true));
        register(new BooleanSetting("Post-Calc", false));
        register(new BooleanSetting("Terrain", false));
        register(new BooleanSetting("Anvils", false));
        register(new NumberSetting<>("FullCalcDelay", 0, 0, 5000));
        register(ICachedDamage.SHOULD_CACHE);
        this.setData(new SafetyData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return Managers.SAFETY.isSafe() ? TextColor.GREEN + "Safe"
                                        : TextColor.RED + "Unsafe";
    }

}
