package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.render.logoutspots.mode.MessageMode;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//TODO: rename into waypoints and add waypoints!
public class LogoutSpots extends Module
{
    protected final Setting<MessageMode> message =
            register(new EnumSetting<>("Message", MessageMode.Render));
    protected final Setting<Boolean> render      =
            register(new BooleanSetting("Render", true));
    protected final Setting<Boolean> friends     =
            register(new BooleanSetting("Friends", true));
    protected final Setting<Float> scale         =
            register(new NumberSetting<>("Scale", 0.003f, 0.001f, 0.01f));

    protected final Map<UUID, LogoutSpot> spots = new ConcurrentHashMap<>();

    public LogoutSpots()
    {
        super("LogoutSpots", Category.Render);
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerJoin(this));
        this.listeners.add(new ListenerLeave(this));
        this.listeners.add(new ListenerRender(this));
    }

    @Override
    protected void onDisable()
    {
        spots.clear();
    }

}
