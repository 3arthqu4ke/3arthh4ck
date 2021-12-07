package me.earth.earthhack.impl.modules.render.trails;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.util.animation.TimeAnimation;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Trails extends Module {

    protected final Setting<Boolean> arrows =
            register(new BooleanSetting("Arrows", false));
    protected final Setting<Boolean> pearls =
            register(new BooleanSetting("Pearls", false));
    protected final Setting<Boolean> snowballs =
            register(new BooleanSetting("Snowballs", false));
    protected final Setting<Integer> time =
            register(new NumberSetting<>("Time", 1, 1, 10));
    protected final ColorSetting color =
            register(new ColorSetting("Color", new Color(255, 0, 0, 255)));
    protected final Setting<Float> width    =
            register(new NumberSetting<>("Width", 1.6f, 0.1f, 10.0f));

    protected Map<Integer, TimeAnimation> ids = new ConcurrentHashMap<>();
    protected Map<Integer, List<Trace>> traceLists = new ConcurrentHashMap<>();
    protected Map<Integer, Trace> traces = new ConcurrentHashMap<>();

    public Trails() {
        super("Trails", Category.Render);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerDestroyEntities(this));
    }

    protected void onEnable() {
        ids = new ConcurrentHashMap<>();
        traces = new ConcurrentHashMap<>();
        traceLists = new ConcurrentHashMap<>();
    }

}
