package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.network.play.client.CPacketEntityAction;

//TODO: MORE MODES, Data!
public class ElytraFlight extends Module
{
    protected final Setting<ElytraMode> mode   =
            register(new EnumSetting<>("Mode", ElytraMode.Wasp));
    protected final Setting<Double> hSpeed     =
            register(new NumberSetting<>("H-Speed", 1.0, 0.0, 100.0));
    protected final Setting<Double> vSpeed     =
            register(new NumberSetting<>("V-Speed", 1.0, 0.0, 100.0));
    protected final Setting<Boolean> autoStart =
            register(new BooleanSetting("AutoStart", false));
    protected final Setting<Boolean> infDura   =
            register(new BooleanSetting("InfiniteDurability", false));
    protected final Setting<Boolean> noWater   =
            register(new BooleanSetting("StopInWater", false));
    protected final Setting<Boolean> noGround  =
            register(new BooleanSetting("StopOnGround", false));
    protected final Setting<Boolean> antiKick  =
            register(new BooleanSetting("AntiKick", false));
    protected final Setting<Float> glide      =
            register(new NumberSetting<>("Glide", 0.0001f, 0.0f, 0.2f));
    protected final Setting<Boolean> ncp =
            register(new BooleanSetting("NCP", false));
    protected final Setting<Boolean> vertical =
            register(new BooleanSetting("Vertical", true));
    protected final Setting<Boolean> accel =
            register(new BooleanSetting("Accelerate", true));
    protected final Setting<Boolean> instant =
            register(new BooleanSetting("Instant", true));
    protected final Setting<Boolean> customPitch =
            register(new BooleanSetting("CustomPitch", true));
    protected final Setting<Double> pitch =
            register(new NumberSetting<>("Pitch", 0.0, -90.0, 90.0));
    protected final Setting<Boolean> rockets =
            register(new BooleanSetting("Firework", false));
    protected final Setting<Double> rocketDelay =
            register(new NumberSetting<>("FireworkDelay", 8.0, 0.1, 15.0));
    protected final Setting<Boolean> rocketSwitchBack =
            register(new BooleanSetting("SwitchBack", false));

    /** A timer to handle AutoStart etc. with. */
    protected final StopWatch timer = new StopWatch();
    protected final StopWatch rocketTimer = new StopWatch();
    protected boolean lag;
    protected double speed;
    protected int kick;

    public ElytraFlight()
    {
        super("ElytraFlight", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerPosLook(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().toString();
    }

    @Override
    protected void onEnable()
    {
        lag = true;
        timer.reset();
        kick = 0;
    }

    public ElytraMode getMode()
    {
        return mode.getValue();
    }

    /**
     * Sends a {@link CPacketEntityAction}, with parameters mc.player and
     * {@link CPacketEntityAction.Action#START_FALL_FLYING}.
     */
    public void sendFallPacket()
    {
        mc.player.connection.sendPacket(new CPacketEntityAction(
                mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
    }
}
