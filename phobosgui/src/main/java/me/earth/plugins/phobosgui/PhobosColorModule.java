package me.earth.plugins.phobosgui;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.plugins.phobosgui.util.PhobosColorUtil;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PhobosColorModule extends Module
{
    private static final PhobosColorModule INSTANCE = new PhobosColorModule();

    public Setting<Boolean> rainbow = register(new BooleanSetting("Rainbow", true));
    public Setting<Integer> rainbowSpeed = register(new NumberSetting<>(
            "Speed", 80, 0, 100));
    public Setting<Integer> rainbowSaturation = register(new NumberSetting<>(
            "Saturation", 255, 0, 255));
    public Setting<Integer> rainbowBrightness = register(new NumberSetting<>(
            "Brightness", 255, 0, 255));
    public Setting<Integer> red = register(new NumberSetting<>("Red", 255, 0,
            255));
    public Setting<Integer> green = register(new NumberSetting<>("Green", 255
            , 0, 255));
    public Setting<Integer> blue = register(new NumberSetting<>("Blue", 255,
            0, 255));
    public Setting<Integer> alpha = register(new NumberSetting<>("Alpha", 255
            , 0, 255));

    public final Map<Integer, Integer> colorHeightMap = new HashMap<>();
    public float hue;

    private PhobosColorModule()
    {
        super("PhobosColors", Category.Client);
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent tickEvent)
            {
                onTick();
            }
        });
        this.enable();
    }

    public static PhobosColorModule getInstance()
    {
        return INSTANCE;
    }

    private void onTick()
    {
        int colorSpeed = 101 - rainbowSpeed.getValue();
        hue = (System.currentTimeMillis() % (360 * colorSpeed)) / (360f * colorSpeed);
        float tempHue = hue;

        for (int i = 0; i <= 510; i++)
        {
            colorHeightMap.put(i, Color.HSBtoRGB(tempHue, rainbowSaturation.getValue() / 255f, rainbowBrightness.getValue() / 255f));
            tempHue += (1f / 765);
        }

        if (PhobosGuiModule.getInstance().colorSync.getValue())
        {
            PhobosColorManager.getInstance().setColor(PhobosColorModule.getInstance().getCurrentColor().getRed(), PhobosColorModule.getInstance().getCurrentColor().getGreen(), PhobosColorModule.getInstance().getCurrentColor().getBlue(), PhobosGuiModule.getInstance().hoverAlpha.getValue());
        }
    }

    public int getCurrentColorHex()
    {
        if (rainbow.getValue())
        {
            return Color.HSBtoRGB(hue, rainbowSaturation.getValue() / 255f, rainbowBrightness.getValue() / 255f);
        }
        else
        {
            return PhobosColorUtil.toARGB(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
        }
    }

    public Color getCurrentColor()
    {
        if (rainbow.getValue())
        {
            return Color.getHSBColor(hue, rainbowSaturation.getValue() / 255f, rainbowBrightness.getValue() / 255f);
        }
        else
        {
            return new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
        }
    }

}
