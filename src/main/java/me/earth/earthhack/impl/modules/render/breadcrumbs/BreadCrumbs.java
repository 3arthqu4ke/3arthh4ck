package me.earth.earthhack.impl.modules.render.breadcrumbs;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.render.ColorModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BreadCrumbs extends ColorModule
{
    public static final Vec3d ORIGIN = new Vec3d(8.0, 64.0, 8.0);

    protected final Setting<Boolean> render =
            register(new BooleanSetting("Render", true));
    protected final Setting<Integer> delay  =
            register(new NumberSetting<>("Delay", 0, 0, 10000));
    protected final Setting<Float> width    =
            register(new NumberSetting<>("Width", 1.6f, 0.1f, 10.0f));
    protected final Setting<Integer> fadeDelay  =
            register(new NumberSetting<>("Fade-Delay", 2000, 0, 10000));
    protected final Setting<Boolean> clearD =
            register(new BooleanSetting("Death-Clear", true));
    protected final Setting<Boolean> clearL =
            register(new BooleanSetting("Logout-Clear", true));
    protected final Setting<Boolean> fade =
            register(new BooleanSetting("Fade", false));
    protected final Setting<Boolean> players =
            register(new BooleanSetting("Players", false));

    protected final StopWatch timer = new StopWatch();
    protected final List<Trace> positions = new ArrayList<>();
    //protected final Map<EntityPlayer>
    protected Trace trace;

    public BreadCrumbs()
    {
        super("BreadCrumbs", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerDeath(this));
        SimpleData data = new SimpleData(this, "Shows where you came from.");
        data.register(color,
                "The color the path will be rendered in.");
        data.register(render,
                "If the path should be rendered.");
        data.register(delay,
                "Intervals in which the BreadCrumbs aren't drawn.");
        data.register(fadeDelay,
                "Delay at which the breadcrumb fades away.");
        data.register(width, "Width of the rendered path.");
        data.register(clearD, "Clears the path when you die.");
        data.register(clearL,
                "Clears the path when you disconnect from the server.");
        data.register(fade,
                "Makes the breadcrumb fade away.");
        this.setData(data);
        this.color.setValue(new Color(255, 0, 0, 125));
    }

    @Override
    protected void onDisable()
    {
        positions.clear();
        trace = null;
    }

}
