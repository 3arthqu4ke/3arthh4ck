package me.earth.earthhack.impl.modules.render.crystalscale;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.animation.TimeAnimation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CrystalScale extends Module
{
    public final Setting<Float> scale     =
            register(new NumberSetting<>("Scale", 1.0f, 0.1f, 2.0f));
    public final Setting<Boolean> animate =
            register(new BooleanSetting("Animate", false));
    public final Setting<Integer> time    =
            register(new NumberSetting<>("AnimationTime", 200, 1, 500));
    public final Map<Integer, TimeAnimation> scaleMap =
            new ConcurrentHashMap<>();

    public CrystalScale()
    {
        super("CrystalScale", Category.Render);
        this.listeners.add(new ListenerDestroyEntities(this));
        this.listeners.add(new ListenerSpawnObject(this));
    }

}
