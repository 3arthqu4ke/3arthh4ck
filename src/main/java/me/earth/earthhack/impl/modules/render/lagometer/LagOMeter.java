package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LagOMeter extends BlockESPModule
{
    protected final Setting<Boolean> esp =
        registerBefore(new BooleanSetting("ESP", true), super.color);
    protected final Setting<Boolean> response =
        registerBefore(new BooleanSetting("Response", true), super.color);
    protected final Setting<Boolean> lagTime =
        registerBefore(new BooleanSetting("Lag", true), super.color);

    protected final Setting<Boolean> nametag =
        register(new BooleanSetting("Nametag", false));
    protected final Setting<Float> scale =
        register(new NumberSetting<>("Scale", 0.003f, 0.001f, 0.01f));
    protected final ColorSetting textColor =
        register(new ColorSetting("Name-Color", new Color(255, 255, 255, 255)));
    protected final Setting<Integer> responseTime =
        register(new NumberSetting<>("ResponseTime", 500, 0, 2500));
    protected final Setting<Integer> time =
        register(new NumberSetting<>("ESP-Time", 500, 0, 2500));
    protected final Setting<Integer> chatTime =
        register(new NumberSetting<>("Chat-Time", 3000, 0, 5000));
    protected final Setting<Boolean> chat =
        register(new BooleanSetting("Chat", true));
    protected final Setting<Boolean> render =
        register(new BooleanSetting("Render-Text", true));

    protected final AtomicBoolean teleported = new AtomicBoolean();
    protected final StopWatch lag = new StopWatch();

    protected ScaledResolution resolution;

    protected String respondingMessage;
    protected String lagMessage;

    protected boolean sent;

    protected double x;
    protected double y;
    protected double z;
    protected float yaw;
    protected float pitch;

    public LagOMeter()
    {
        super("Lag-O-Meter", Category.Render);
        this.unregister(super.height);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTeleport(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerText(this));
        this.listeners.add(new ListenerTick(this));
        super.color.setValue(new Color(255, 0, 0, 80));
        super.outline.setValue(new Color(255, 0, 0, 255));
        this.setData(new LagOMeterData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        long t = System.currentTimeMillis() - Managers.NCP.getTimeStamp();
        if (t > time.getValue())
        {
            return null;
        }

        return TextColor.RED + MathUtil.round(t / 1000.0, 1);
    }

    @Override
    protected void onEnable()
    {
        sent = false;
        teleported.set(true);
        resolution = new ScaledResolution(mc);
        x = Managers.POSITION.getX();
        y = Managers.POSITION.getY();
        z = Managers.POSITION.getZ();
        yaw = Managers.ROTATION.getServerYaw();
        pitch = Managers.ROTATION.getServerPitch();
    }

}
