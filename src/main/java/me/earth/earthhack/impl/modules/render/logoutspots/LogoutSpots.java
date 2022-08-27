package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.render.logoutspots.mode.MessageMode;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;

import java.awt.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//TODO: rename into waypoints and add waypoints!
public class LogoutSpots extends BlockESPModule
{
    protected final Setting<Color> fill          =
            register(new ColorSetting("Fill", new Color(255, 0, 0, 155)));
    protected final Setting<MessageMode> message =
            register(new EnumSetting<>("Message", MessageMode.Render));
    protected final Setting<Boolean> render      =
            register(new BooleanSetting("Render", true));
    protected final Setting<Boolean> chams       =
            register(new BooleanSetting("Chams", false));
    protected final Setting<Boolean> box         =
            register(new BooleanSetting("Box", true));
    protected final Setting<Boolean> nametags     =
            register(new BooleanSetting("Nametags", false));
    protected final Setting<Boolean> friends     =
            register(new BooleanSetting("Friends", true));
    protected final Setting<Float> scale         =
            register(new NumberSetting<>("Scale", 0.003f, 0.001f, 0.01f))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> remove      =
            register(new NumberSetting<>("Remove", 0, 0, 300))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> time        =
            register(new BooleanSetting("Time", false))
                .setComplexity(Complexity.Medium);

    protected final Map<UUID, LogoutSpot> spots = new ConcurrentHashMap<>();

    public LogoutSpots()
    {
        super("LogoutSpots", Category.Render);
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerJoin(this));
        this.listeners.add(new ListenerLeave(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTick(this));
        this.color.setValue(new Color(255, 0, 0, 255));
        this.outline.setValue(new Color(255, 0, 0, 255));
        this.unregister(this.height);
        this.setData(new LogoutSpotsData(this));
    }

    @Override
    protected void onDisable()
    {
        spots.clear();
    }

}
