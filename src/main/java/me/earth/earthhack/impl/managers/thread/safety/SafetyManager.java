package me.earth.earthhack.impl.managers.thread.safety;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.client.safety.util.Update;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.util.math.Timer;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages Safety so we don't die.
 */
public class SafetyManager extends SubscriberImpl implements Globals
{
    private final AtomicBoolean safe = new AtomicBoolean(false);

    private static final ModuleCache<Suicide> SUICIDE =
     Caches.getModule(Suicide.class);
    protected final SettingCache<Boolean, BooleanSetting, Safety> newV =
     Caches.getSetting(Safety.class, BooleanSetting.class, "1.13+", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> newVEntities =
     Caches.getSetting(Safety.class, BooleanSetting.class, "1.13-Entities", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> beds =
     Caches.getSetting(Safety.class, BooleanSetting.class, "BedCheck", false);
    protected final SettingCache<Float, NumberSetting<Float>, Safety> damage =
     Caches.getSetting(Safety.class, Setting.class, "MaxDamage", 4.0f);
    protected final SettingCache<Integer, NumberSetting<Integer>, Safety> d =
     Caches.getSetting(Safety.class, Setting.class, "Delay", 25);
    protected final SettingCache<Update, EnumSetting<Update>, Safety> mode =
     Caches.getSetting(Safety.class, Setting.class, "Updates", Update.Tick);
    protected final SettingCache<Boolean, BooleanSetting, Safety> longs =
     Caches.getSetting(Safety.class, BooleanSetting.class, "2x1s", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> big =
     Caches.getSetting(Safety.class, BooleanSetting.class, "2x2s", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> post =
     Caches.getSetting(Safety.class, BooleanSetting.class, "Post-Calc", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> anvils =
     Caches.getSetting(Safety.class, BooleanSetting.class, "Anvils", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> terrain =
     Caches.getSetting(Safety.class, BooleanSetting.class, "Terrain", false);
    protected final SettingCache<Integer, NumberSetting<Integer>, Safety> fullCalc =
     Caches.getSetting(Safety.class, Setting.class, "FullCalcDelay", 0);
    private final Timer fullCalcTimer = new Timer();

    /**
     * Constructs a new SafetyManager.
     */
    public SafetyManager()
    {
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerMotionUpdate(this));
    }

    /**
     * @return <tt>true</tt> if there are no crystals or positions for
     *          crystals in range that could kill us.
     */
    public boolean isSafe()
    {
        return SUICIDE.isEnabled() || safe.get();
    }

    /**
     * Only set to <tt>true</tt> if you are sure we can't die.
     *
     * @param safeIn will be returned by {@link SafetyManager#isSafe()}.
     */
    public void setSafe(boolean safeIn)
    {
        this.safe.set(safeIn);
    }

    /**
     * Runs a {@link SafetyRunnable} to check if we are safe.
     */
    protected void runThread()
    {
        if (mc.player != null && mc.world != null)
        {
            List<Entity> crystals = new ArrayList<>(mc.world.loadedEntityList);
            SafetyRunnable runnable = new SafetyRunnable(this,
                                                         crystals,
                                                         newVEntities.getValue(),
                                                         newV.getValue(),
                                                         beds.getValue(),
                                                         damage.getValue(),
                                                         longs.getValue(),
                                                         big.getValue(),
                                                         anvils.getValue(),
                                                         terrain.getValue(),
                                                         fullCalcTimer,
                                                         fullCalc.getValue());
            Managers.THREAD.submit(runnable);
        }
    }

}
