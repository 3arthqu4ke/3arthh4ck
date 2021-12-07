package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.modules.movement.jesus.mode.JesusMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.StopWatch;

public class Jesus extends Module implements CollisionEvent.Listener
{
    protected final Setting<JesusMode> mode =
            register(new EnumSetting<>("Mode", JesusMode.Solid));

    protected final ListenerCollision listenerCollision;
    /** Timer to prevent us from jesusing directly after we logged in */
    protected final StopWatch timer = new StopWatch();
    /** Manages trampoline jumps */
    protected boolean jumped;

    public Jesus()
    {
        super("Jesus", Category.Movement);
        this.listenerCollision = new ListenerCollision(this);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerLiquidJump(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerTick(this));
        SimpleData data = new SimpleData(this, "Walk on water like Jesus.");
        data.register(mode, "-Solid just walk on water.\n" +
                "-Trampoline makes you jump high on water." +
                "\n-Dolphin mini jumps.");
        this.setData(data);
    }

    @Override
    public void onCollision(CollisionEvent event)
    {
        if (this.isEnabled())
        {
            listenerCollision.invoke(event);
        }
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().toString();
    }

}
