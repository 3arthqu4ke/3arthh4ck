package me.earth.earthhack.impl.modules.render.crosshair;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.render.CrosshairEvent;
import me.earth.earthhack.impl.modules.render.crosshair.mode.GapMode;
import me.earth.earthhack.impl.util.client.SimpleData;

import java.awt.*;

/**
 * @author Gerald
 * @since 6/17/2021
 **/
public class Crosshair extends Module
{
    protected final Setting<Boolean> indicator =
            register(new BooleanSetting("Attack-Indicator", true));
    protected final Setting<Boolean> outline =
            register(new BooleanSetting("Outline", true));
    protected final Setting<GapMode> gapMode        =
            register(new EnumSetting<>("Gap-Mode", GapMode.NORMAL));
    protected final Setting<Color> color =
            register(new ColorSetting("Color", new Color(190,60,190)));
    protected final Setting<Color> outlineColor =
            register(new ColorSetting("Outline-Color", new Color(0,0,0)));
    protected final Setting<Float> length =
            register(new NumberSetting<>("Length", 5.5f, 0.5f, 50.f));
    protected final Setting<Float> gapSize =
            register(new NumberSetting<>("Gap-Size", 2.0f, 0.5f, 20.f));
    protected final Setting<Float> width =
            register(new NumberSetting<>("Width", 0.5f, 0.1f, 10.f));

    public Crosshair()
    {
        super("Crosshair", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new EventListener<CrosshairEvent>(CrosshairEvent.class)
        {
            @Override
            public void invoke(CrosshairEvent event)
            {
                event.setCancelled(true);
            }
        });

        SimpleData data = new SimpleData(this,
                "Gives you a custom crosshair.");

        this.setData(data);

    }
}
