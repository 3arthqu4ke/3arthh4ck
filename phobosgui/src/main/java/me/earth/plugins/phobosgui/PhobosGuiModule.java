package me.earth.plugins.phobosgui;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.plugins.phobosgui.gui.PhobosGui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PhobosGuiModule extends Module
{
    private static final PhobosGuiModule INSTANCE = new PhobosGuiModule();

    public Setting<Boolean> colorSync = register(new BooleanSetting("Sync", true));
    public Setting<Boolean> outline = register(new BooleanSetting("Outline", false));
    public Setting<Boolean> rainbowRolling = register(new BooleanSetting("RollingRainbow", true));
    public Setting<Integer> hoverAlpha = register(new NumberSetting<>("Alpha", 180, 0, 255));
    public Setting<Integer> alpha = register(new NumberSetting<>("HoverAlpha", 240, 0, 255));
    public Setting<Boolean> openCloseChange = register(new BooleanSetting("Open/Close", true));
    public Setting<String> open = register(new StringSetting("Open:", "-"));
    public Setting<String> close = register(new StringSetting("Close:", "+"));
    public Setting<String> moduleButton = register(new StringSetting("Buttons:", ""));
    public Setting<Boolean> devSettings = register(new BooleanSetting("DevSettings", true));
    public Setting<Integer> topRed = register(new NumberSetting<>("TopRed", 255, 0, 255));
    public Setting<Integer> topGreen = register(new NumberSetting<>("TopGreen", 0, 0, 255));
    public Setting<Integer> topBlue = register(new NumberSetting<>("TopBlue", 0, 0, 255));
    public Setting<Integer> topAlpha = register(new NumberSetting<>("TopAlpha", 255, 0, 255));
    public Setting<Boolean> rainbow = register(new BooleanSetting("Rainbow", false));
    public Setting<Integer> factor = register(new NumberSetting<>("Factor", 1, 0, 20));
    public Setting<Integer> rainbowSpeed = register(new NumberSetting<>("RSpeed", 20, 0, 100));
    public Setting<Integer> rainbowSaturation = register(new NumberSetting<>("Saturation", 255, 0, 255));
    public Setting<Integer> rainbowBrightness = register(new NumberSetting<>("Brightness", 255, 0, 255));

    public final Map<Integer, Integer> colorMap = new HashMap<>();

    private PhobosGuiModule()
    {
        super("PhobosGui", Category.Client);
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent tickEvent)
            {
                if (!(mc.currentScreen instanceof PhobosGui))
                {
                    disable();
                }
            }
        });
        this.listeners.add(new EventListener<Render2DEvent>(Render2DEvent.class)
        {
            @Override
            public void invoke(Render2DEvent event)
            {
                onRender2D();
            }
        });
    }

    public static PhobosGuiModule getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void onEnable()
    {
        PhobosGui gui = new PhobosGui();
        System.out.println(mc + "");
        mc.displayGuiScreen(gui);
    }

    @Override
    protected void onDisable()
    {
        if (mc.currentScreen instanceof PhobosGui)
        {
            mc.displayGuiScreen(null);
        }
    }

    private void onRender2D()
    {
        final int colorSpeed = 101 - rainbowSpeed.getValue();
        final float hue = colorSync.getValue() ? PhobosColorModule.getInstance().hue : (System.currentTimeMillis() % (360 * colorSpeed)) / (360f * colorSpeed);
        int height = PhobosTextManager.getInstance().scaledHeight;

        float tempHue = hue;
        for (int i = 0; i <= height; i++)
        {
            if (colorSync.getValue())
            {
                colorMap.put(i, Color.HSBtoRGB(tempHue, PhobosColorModule.getInstance().rainbowSaturation.getValue() / 255f, PhobosColorModule.getInstance().rainbowBrightness.getValue() / 255f));
            }
            else
            {
                colorMap.put(i, Color.HSBtoRGB(tempHue, rainbowSaturation.getValue() / 255f, rainbowBrightness.getValue() / 255f));
            }
            tempHue += ((1f / height) * factor.getValue());
        }
    }

}
