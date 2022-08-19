package me.earth.earthhack.impl.modules.render.ambience;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.util.render.WorldRenderUtil;
import me.earth.earthhack.vanilla.Environment;

import java.awt.*;
import java.lang.reflect.Field;

public class Ambience extends Module
{

    protected final Setting<Color> color =
            register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    protected final Setting<Boolean> useSaturation =
            register(new BooleanSetting("UseSaturation", false));
    protected final Setting<Float> saturation =
            register(new NumberSetting<>("Saturation", 0.5f, 0.0f, 1.0f));

    protected boolean lightPipeLine;

    public Ambience()
    {
        super("Ambience", Category.Render);
        this.color.addObserver(setting -> loadRenderers());
        if (Environment.hasForge())
        {
            try
            {
                Field field = Class
                        .forName("net.minecraftforge.common.ForgeModContainer",
                                true, this.getClass().getClassLoader())
                        .getDeclaredField("forgeLightPipelineEnabled");

                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                this.lightPipeLine = field.getBoolean(null);
                field.setAccessible(accessible);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public Color getColor()
    {
        return new Color(color.getValue().getRed(), color.getValue().getGreen(), color.getValue().getBlue(), color.getValue().getAlpha());
    }

    public boolean useSaturation()
    {
        return useSaturation.getValue();
    }

    public float getSaturation()
    {
        return saturation.getValue();
    }

    @Override
    protected void onEnable()
    {
        if (Environment.hasForge())
        {
            try
            {
                Field field = Class
                        .forName("net.minecraftforge.common.ForgeModContainer",
                                true, this.getClass().getClassLoader())
                        .getDeclaredField("forgeLightPipelineEnabled");

                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                this.lightPipeLine = field.getBoolean(null);
                field.set(null, false);
                field.setAccessible(accessible);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        loadRenderers();
        ((IEntityRenderer) mc.entityRenderer).setLightmapUpdateNeeded(true);
    }

    @Override
    public void onDisable()
    {
        if (Environment.hasForge())
        {
            try
            {
                Field field = Class
                        .forName("net.minecraftforge.common.ForgeModContainer",
                                true, this.getClass().getClassLoader())
                        .getDeclaredField("forgeLightPipelineEnabled");

                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(null, this.lightPipeLine);
                field.setAccessible(accessible);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        loadRenderers();
    }

    public void loadRenderers()
    {
        if (mc.world != null
                && mc.player != null
                && mc.renderGlobal != null
                && mc.gameSettings != null)
        {
            WorldRenderUtil.reload(true);
        }
    }

}
