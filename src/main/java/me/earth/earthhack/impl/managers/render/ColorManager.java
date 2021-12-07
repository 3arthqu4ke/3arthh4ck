package me.earth.earthhack.impl.managers.render;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;

import java.awt.*;

public class ColorManager extends SubscriberImpl
{
    private final Setting<Integer> speed =
        new NumberSetting<>("RainbowSpeed", 50, 0, 100);
    private final Setting<Color> color =
            new ColorSetting("Color", new Color(127, 66, 186));
    private Color universal;
    private float hue;

    public ColorManager()
    {
        this.universal = new Color(255, 255, 255, 255);
        this.listeners.add(new EventListener<TickEvent>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent event)
            {
                update();
            }
        });
    }

    private void update()
    {
        if (speed.getValue() == 0)
        {
            return;
        }

        hue = (System.currentTimeMillis() % (360 * speed.getValue()))
                / (360.0f * speed.getValue());
    }

    public void setUniversal(Color color)
    {
        this.universal = color;
    }

    public Color getUniversal()
    {
        return universal;
    }

    public float getHue()
    {
        return hue;
    }

    public float getHueByPosition(double pos)
    {
        return (float) (hue - pos * 0.001f);
    }

    public Setting<Integer> getRainbowSpeed()
    {
        return speed;
    }

    public Setting<Color> getColorSetting()
    {
        return color;
    }

}
