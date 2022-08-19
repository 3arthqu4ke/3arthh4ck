package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.timer.mode.TimerMode;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.TextColor;

//TODO: For Mode Blink: check the way network packets update, theres something
// with like 20 ticks until it sets the position
public class Timer extends DisablingModule
{
    protected final Setting<TimerMode> mode   =
            register(new EnumSetting<>("Mode", TimerMode.Normal));
    protected final Setting<Integer> autoOff  =
            register(new NumberSetting<>("AutoOff", 0, 0, 1000));
    protected final Setting<Integer> lagTime  =
            register(new NumberSetting<>("LagTime", 250, 0, 1000));
    protected final Setting<Float> speed      =
            register(new NumberSetting<>("Speed", 1.0888f, 0.1f, 100.0f));
    // TODO: make this like normal timer by checking every gameloop.
    protected final Setting<Integer> updates  =
            register(new NumberSetting<>("Updates", 2, 0, 100));
    protected final Setting<Float> fast       =
            register(new NumberSetting<>("Fast", 20.0f, 0.1f, 100.0f));
    protected final Setting<Integer> fastTime =
            register(new NumberSetting<>("FastTime", 100, 0, 5000));
    protected final Setting<Float> slow       =
            register(new NumberSetting<>("Slow", 1.0f, 0.1f, 100.0f));
    protected final Setting<Integer> slowTime =
            register(new NumberSetting<>("SlowTime", 250, 0, 5000));
    protected final Setting<Integer> maxPackets =
            register(new NumberSetting<>("Max-Packets", 100, 0, 1000));
    protected final Setting<Integer> offset =
            register(new NumberSetting<>("Offset", 10, 0, 100));
    protected final Setting<Integer> letThrough =
            register(new NumberSetting<>("Network-Ticks", 10, 0, 100));

    protected final StopWatch offTimer    = new StopWatch();
    protected final StopWatch switchTimer = new StopWatch();
    protected float pSpeed = 1.0f;
    protected int ticks   = 0;
    protected int packets = 0;
    protected int sent    = 0;
    protected boolean isSlow;

    public Timer()
    {
        super("Timer", Category.Player);
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.addAll(new ListenerPlayerPackets(this).getListeners());
        this.setData(new TimerData(this));
    }

    @Override
    protected void onEnable()
    {
        packets = 0;
        sent    = 0;
        isSlow = false;
        offTimer.reset();
    }

    @Override
    protected void onDisable()
    {
        Managers.TIMER.reset();
    }

    @Override
    public String getDisplayInfo()
    {
        String color;

        if (!Managers.NCP.passed(lagTime.getValue()))
        {
            color = TextColor.RED;
        }
        else
        {
            color = "";
        }

        switch(mode.getValue())
        {
            case Switch:
                return color + getSwitchSpeed();
            case Physics:
                return color + "Physics";
            case Blink:
                return (packets > 0 && pSpeed != 1.0f ? TextColor.GREEN : color)
                        + packets;
            default:
        }

        return color + speed.getValue().toString();
    }

    public float getSpeed()
    {
        if (Managers.NCP.passed(lagTime.getValue()))
        {
            switch(mode.getValue())
            {
                case Switch:
                    if (switchTimer.passed(getTime()))
                    {
                        isSlow = !isSlow;
                        switchTimer.reset();
                    }
                    return getSwitchSpeed();
                case Normal:
                    return speed.getValue();
                case Blink:
                    return pSpeed;
                default:
            }
        }

        return 1.0f;
    }

    private int getTime()
    {
        return isSlow ? slowTime.getValue() : fastTime.getValue();
    }

    private float getSwitchSpeed()
    {
        return isSlow ? slow.getValue() : fast.getValue();
    }

}
