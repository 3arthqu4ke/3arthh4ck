//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.earth.skycolor;

import java.awt.Color;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;







public class SkyColor extends Module {
    protected final Setting<Color> color = this.register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    protected final Setting<Boolean> fog = this.register(new BooleanSetting("fog", false));

    public SkyColor() {
        super("SkyColor", Category.Render);
        this.setData(new SimpleData(this, "Changes Sky Color"));
    }

    @SubscribeEvent
    public void fogColors(EntityViewRenderEvent.FogColors event) {
        event.setRed((float)((Color)this.color.getValue()).getRed() / 255.0F);
        event.setGreen((float)((Color)this.color.getValue()).getGreen() / 255.0F);
        event.setBlue((float)((Color)this.color.getValue()).getBlue() / 255.0F);
    }

    @SubscribeEvent
    public void fog_density(EntityViewRenderEvent.FogDensity event) {
        if ((Boolean)this.fog.getValue()) {
            event.setDensity(0.0F);
            event.setCanceled(true);
        }

    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }



    }


