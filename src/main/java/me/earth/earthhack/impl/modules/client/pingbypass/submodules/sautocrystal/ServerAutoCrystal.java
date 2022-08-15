package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SimpleSoundObserver;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassSubmodule;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;

public class ServerAutoCrystal extends PingBypassSubmodule
{
    protected final Setting<Boolean> soundR =
            register(new BooleanSetting("SoundRemove", false));

    protected final SoundObserver observer;
    protected final StopWatch timer = new StopWatch();
    protected BlockPos renderPos;

    public ServerAutoCrystal(PingBypassModule pingBypass)
    {
        super(pingBypass, "S-AutoCrystal", Category.Client);
        register(new BooleanSetting("Place", true));
        register(new EnumSetting<>("Target", Target.Closest));
        register(new NumberSetting<>("PlaceRange", 6.0f, 0.0f, 6.0f));
        register(new NumberSetting<>("PlaceTrace", 6.0f, 0.0f, 6.0f));
        register(new NumberSetting<>("MinDamage", 6.0f, 0.0f, 20.0f));
        register(new NumberSetting<>("PlaceDelay", 0, 0, 500));
        register(new NumberSetting<>("MaxSelfPlace", 9.0f, 0.0f, 20.0f));
        register(new NumberSetting<>("FacePlace", 10.0f, 0.0f, 36.0f));
        register(new NumberSetting<>("MultiPlace", 1, 1, 5));
        register(new BooleanSetting("CountMin", true));
        register(new BooleanSetting("AntiSurround", true));
        register(new BooleanSetting("1.13+", false));
        register(new BooleanSetting("Break", true));
        register(new NumberSetting<>("BreakRange", 6.0f, 0.0f, 6.0f));
        register(new NumberSetting<>("BreakTrace", 4.5f, 0.0f, 6.0f));
        register(new NumberSetting<>("BreakDelay", 0, 0, 500));
        register(new NumberSetting<>("MaxSelfBreak", 10.0f, 0.0f, 20.0f));
        register(new BooleanSetting("Instant", false));
        register(new EnumSetting<>("Rotate", ACRotate.None));
        register(new BooleanSetting("MultiThread", false));
        register(new BooleanSetting("Suicide", false));
        register(new BooleanSetting("Stay", false));
        register(new NumberSetting<>("Range", 12.0f, 6.0f, 12.0f));
        register(new BooleanSetting("Override", false));
        register(new NumberSetting<>("MinFace", 2.0f, 0.1f, 4.0f));
        register(new BooleanSetting("AntiFriendPop", true));
        register(new NumberSetting<>("Cooldown", 500, 0, 500));
        register(new BooleanSetting("MultiTask", true));
        register(new NumberSetting<>("CombinedTrace", 4.5f, 0.0f, 6.0f));
        register(new BooleanSetting("FallBack", true));
        register(new NumberSetting<>("FB-Dmg", 2.0f, 0.0f, 6.0f));
        register(new BooleanSetting("Tick", true));
        register(new BooleanSetting("SetDead", false));
        register(new NumberSetting<>("ThreadDelay", 30, 0, 100));
        register(new BooleanSetting("Post-Tick", false));
        register(new BooleanSetting("Gameloop", false));
        register(new BooleanSetting("Packet", true));

        this.listeners.add(new ListenerRotations(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerRenderPos(this));
        this.listeners.add(new ListenerTick(this));

        this.observer = new SimpleSoundObserver(this.soundR::getValue);

        this.setData(new ServerAutoCrystalData(this));
    }

    @Override
    protected void onEnable()
    {
        Managers.SET_DEAD.addObserver(observer);
    }

    @Override
    protected void onDisable()
    {
        Managers.SET_DEAD.removeObserver(observer);
    }

    protected void onTick()
    {
        if (!this.getParent().isEnabled())
        {
            return;
        }

        if (timer.passed(1000)
                || mc.player == null
                || !InventoryUtil.isHolding(Items.END_CRYSTAL))
        {
            renderPos = null;
            timer.reset();
        }

        if (mc.player != null
                && InventoryUtil.isHolding(Items.END_CRYSTAL)
                && !InventoryUtil.isHoldingServer(Items.END_CRYSTAL)
                && this.getParent().isEnabled())
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, InventoryUtil::syncItem);
        }
    }

}
