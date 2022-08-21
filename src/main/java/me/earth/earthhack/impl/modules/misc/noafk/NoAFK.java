package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.math.BlockPos;

//TODO: Maybe AutoReply from File? CPacketTabComplete?
public class NoAFK extends Module
{
    /** Default Message for AutoReply. */
    private static final String DEFAULT =
            "I'm AFK! This message was brought to you by 3arthh4ck.";

    protected final Setting<Boolean> rotate    =
            register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> swing     =
            register(new BooleanSetting("Swing", true));
    protected final Setting<Boolean> sneak     =
            register(new BooleanSetting("Sneak", true));
    protected final Setting<Boolean> jump     =
            register(new BooleanSetting("Jump", false));
    protected final Setting<Integer> jumpDelay =
            register(new NumberSetting<>("Jump-DelayS", 5, 1, 1000));
    protected final Setting<Boolean> autoReply =
            register(new BooleanSetting("AutoReply", false));
    protected final Setting<String> message    =
            register(new StringSetting("Message", DEFAULT));
    protected final Setting<String> indicator  =
            register(new StringSetting("Indicator", " whispers: "));
    protected final Setting<String> reply      =
            register(new StringSetting("Reply", "/r "));
    protected final Setting<TextColor> color   =
            register(new EnumSetting<>("Color", TextColor.LightPurple));
    protected final Setting<Integer> lagTime =
            register(new NumberSetting<>("Lag-Time", 2000, 0, 10_000));

    protected final Setting<Boolean> walk =
            register(new BooleanSetting("Walk", false));
    protected final Setting<Integer> walkFor =
            register(new NumberSetting<>("Walk-For", 60, 5, 600));
    protected final Setting<Integer> waitFor =
            register(new NumberSetting<>("Wait-For", 15, 5, 600));
    protected final Setting<Float> yaw =
            register(new NumberSetting<>("Yaw", 90f, 0f, 180f));
    protected final Setting<Boolean> randomlyBackwards =
            register(new BooleanSetting("RandomlyBackwards", false));

    // TODO: into plugin?
    protected final Setting<Boolean> baritone =
        register(new BooleanSetting("Baritone", false));
    protected final Setting<Integer> baritoneDelay =
        register(new NumberSetting<>("Baritone-Delay", 60, 5, 600));
    protected final Setting<Integer> baritoneRange =
        register(new NumberSetting<>("Baritone-Range", 200, 1, 1000));
    protected final Setting<String> baritonePrefix =
        register(new StringSetting("BaritonePrefix", "#"));

    protected final StopWatch walkTimer = new StopWatch();
    protected final StopWatch jumpTimer = new StopWatch();
    /** Timer to handle Swing Delay with */
    protected final StopWatch swing_timer = new StopWatch();
    /** Timer to handle Sneak Delay with */
    protected final StopWatch sneak_timer = new StopWatch();
    /** Handles sneaking. */
    protected boolean sneaking;
    protected boolean walking;

    protected final StopWatch baritoneTimer = new StopWatch();
    protected boolean blockingChatMessages = false;
    protected Stage stage = Stage.GO;
    protected BlockPos startPos;
    protected BlockPos target;

    public NoAFK()
    {
        super("NoAFK", Category.Misc);
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerChat(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerSendChat(this));
        this.setData(new NoAFKData(this));
    }

    @Override
    protected void onEnable() {
        blockingChatMessages = false;
        stage = Stage.BACK;
        baritoneTimer.setTime(0);
        startPos = null;
        target = null;
        walking = false;
    }

    public enum Stage {
        GO,
        BACK
    }

}
