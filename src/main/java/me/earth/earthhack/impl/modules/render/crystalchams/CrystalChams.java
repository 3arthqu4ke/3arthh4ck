package me.earth.earthhack.impl.modules.render.crystalchams;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.modules.render.handchams.modes.ChamsMode;

import java.awt.*;

public class CrystalChams extends Module {

    public final Setting<ChamsMode> mode       =
            register(new EnumSetting<>("Mode", ChamsMode.Normal));
    public final Setting<Boolean> chams        =
            register(new BooleanSetting("Chams", false));
    public final Setting<Boolean> throughWalls =
            register(new BooleanSetting("ThroughWalls", false));
    public final Setting<Boolean> wireframe    =
            register(new BooleanSetting("Wireframe", false));
    public final Setting<Boolean> wireWalls    =
            register(new BooleanSetting("WireThroughWalls", false));
    public final Setting<Color> color          =
            register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> wireFrameColor =
            register(new ColorSetting("WireframeColor", new Color(255, 255, 255, 255)));


    public CrystalChams() {
        super("CrystalChams", Category.Render);
    }

}
