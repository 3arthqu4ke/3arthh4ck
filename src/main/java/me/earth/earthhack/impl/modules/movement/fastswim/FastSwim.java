package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;

public class FastSwim extends Module
{
    protected final Setting<Double> vWater =
            register(new NumberSetting<>("V-Water", 1.0, 0.1, 20.0));
    protected final Setting<Double> downWater =
            register(new NumberSetting<>("Down-Water", 1.0, 0.1, 20.0));
    protected final Setting<Double> hWater =
            register(new NumberSetting<>("H-Water", 1.0, 0.1, 20.0));
    protected final Setting<Double> vLava  =
            register(new NumberSetting<>("Up-Lava", 1.0, 0.1, 20.0));
    protected final Setting<Double> downLava  =
            register(new NumberSetting<>("Down-Lava", 1.0, 0.1, 20.0));
    protected final Setting<Double> hLava  =
            register(new NumberSetting<>("H-Lava", 1.0, 0.1, 20.0));
    protected final Setting<Boolean> strafe  =
            register(new BooleanSetting("Strafe", false));
    protected final Setting<Boolean> fall  =
            register(new BooleanSetting("Fall", false));
    protected final Setting<Boolean> accelerate  =
            register(new BooleanSetting("Accelerate", false));
    protected final Setting<Double> accelerateFactor  =
            register(new NumberSetting<>("Factor", 1.1, 0.1, 20.0));

    /* Speed for accelerate mode. */
    protected double lavaSpeed;
    protected double waterSpeed;

    public FastSwim()
    {
        super("FastSwim", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.setData(new FastSwimData(this));
    }

}
