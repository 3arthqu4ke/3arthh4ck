package me.earth.earthhack.impl.modules.misc.antiaim;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.item.ItemFood;

public class AntiAim extends Module
{
    protected final Setting<AntiAimMode> mode =
            register(new EnumSetting<>("Mode", AntiAimMode.Spin));
    protected final Setting<Float> hSpeed =
            register(new NumberSetting<>("H-Speed", 10.0f, 0.1f, 180.0f));
    protected final Setting<Float> vSpeed =
            register(new NumberSetting<>("V-Speed", 10.0f, 0.1f, 180.0f));
    protected final Setting<Boolean> strict =
            register(new BooleanSetting("Strict", true));
    protected final Setting<Boolean> sneak =
            register(new BooleanSetting("Sneak", false));
    protected final Setting<Integer> sneakDelay =
            register(new NumberSetting<>("Sneak-Delay", 500, 0, 5000));
    protected final Setting<Float> yaw =
            register(new NumberSetting<>("Yaw", 0.0f, -360.0f, 360.0f));
    protected final Setting<Float> pitch =
            register(new NumberSetting<>("Pitch", 0.0f, -90.0f, 90.0f));
    protected final Setting<Integer> skip =
            register(new NumberSetting<>("Skip", 1, 1, 20));
    protected final Setting<Boolean> flipYaw =
            register(new BooleanSetting("FlipYaw", true));
    protected final Setting<Boolean> flipPitch =
            register(new BooleanSetting("FlipPitch", true));

    protected final StopWatch timer = new StopWatch();
    protected float lastYaw;
    protected float lastPitch;

    public AntiAim()
    {
        super("AntiAim", Category.Misc);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerInput(this));
        this.setData(new AntiAimData(this));
    }

    @Override
    protected void onEnable()
    {
        if (mc.player != null)
        {
            lastYaw = mc.player.rotationYaw;
            lastPitch = mc.player.rotationPitch;
        }
    }

    public boolean dontRotate()
    {
        return strict.getValue()
                && (((!(mc.player.getActiveItemStack().getItem()
                                instanceof ItemFood)
                            || mc.gameSettings.keyBindAttack.isKeyDown())
                        && (mc.gameSettings.keyBindAttack.isKeyDown()
                            || mc.gameSettings.keyBindUseItem.isKeyDown()))
                    || Mouse.isButtonDown(2));
    }

}
