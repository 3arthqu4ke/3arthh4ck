package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.block.MixinTileEntityShulkerBox;
import me.earth.earthhack.impl.core.mixins.entity.MixinEntity;
import me.earth.earthhack.impl.util.math.MathUtil;

/**
 * {@link MixinTileEntityShulkerBox}.
 * {@link MixinEntity}
 */
public class Velocity extends Module
{
    protected final Setting<Boolean> knockBack  =
            register(new BooleanSetting("KnockBack", true));
    protected final Setting<Float> horizontal   =
            register(new NumberSetting<>("Horizontal", 0.0f, 0.0f, 100.0f));
    protected final Setting<Float> vertical     =
            register(new NumberSetting<>("Vertical", 0.0f, 0.0f, 100.0f));
    protected final Setting<Boolean> noPush     =
            register(new BooleanSetting("NoPush", true));
    protected final Setting<Boolean> explosions =
            register(new BooleanSetting("Explosions", true));
    protected final Setting<Boolean> bobbers    =
            register(new BooleanSetting("Bobbers", true));
    protected final Setting<Boolean> water      =
            register(new BooleanSetting("Water", false));
    protected final Setting<Boolean> blocks     =
            register(new BooleanSetting("Blocks", false));
    protected final Setting<Boolean> shulkers   =
            register(new BooleanSetting("Shulkers", false));
    protected final Setting<Boolean> fixPingBypass   =
            register(new BooleanSetting("FixPingBypassPackets", true));
    // TODO: AntiLag!

    public Velocity()
    {
        super("Velocity", Category.Movement);
        this.listeners.add(new ListenerBlockPush(this));
        this.listeners.add(new ListenerEntityVelocity(this));
        this.listeners.add(new ListenerWaterPush(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerBobber(this));
        this.setData(new VelocityData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return "H" + MathUtil.round(horizontal.getValue(), 1)
                + "%V" + MathUtil.round(vertical.getValue(), 1) + "%";
    }

}
