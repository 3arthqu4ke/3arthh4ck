package me.earth.earthhack.impl.modules.render.skeleton;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Skeleton extends Module
{
    protected final Map<EntityPlayer, float[][]> rotations = new HashMap<>();
    public final Setting<Color> color          =
            register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> friendColor =
            register(new ColorSetting("FriendColor", new Color(50, 255, 50, 255)));
    public final Setting<Color> targetColor =
            register(new ColorSetting("TargetColor", new Color(255, 0, 0, 255)));
    public Skeleton()
    {
        super("Skeleton", Category.Render);
        this.listeners.add(new ListenerModel(this));
        this.listeners.add(new ListenerRender(this));
        this.setData(new SimpleData(this, "Spooky."));
    }

}

