package me.earth.earthhack.impl.modules.combat.holefiller;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.managers.thread.holes.IHoleManager;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.List;

public class HoleFiller extends ObbyListenerModule<ListenerObby>
        implements HoleObserver, IHoleManager
{
    protected static final ModuleCache<Offhand> OFFHAND =
        Caches.getModule(Offhand.class);

    protected final Setting<Double> range =
        register(new NumberSetting<>("Range", 5.25, 0.0, 6.0));
    protected final Setting<Integer> disable =
        register(new NumberSetting<>("Disable", 250, 0, 10_000));
    protected final Setting<Boolean> longHoles =
        register(new BooleanSetting("2x1s", false));
    protected final Setting<Boolean> bigHoles =
        register(new BooleanSetting("2x2s", false));
    protected final Setting<Integer> calcDelay =
        register(new NumberSetting<>("CalcDelay", 0, 0, 500));
    protected final Setting<Boolean> requireTarget =
        register(new BooleanSetting("RequireTarget", false));
    protected final Setting<Double> targetRange =
        register(new NumberSetting<>("Target-Range", 6.0, 0.0, 12.0));
    protected final Setting<Double> targetDistance =
        register(new NumberSetting<>("Target-Block", 3.0, 0.0, 12.0));
    protected final Setting<Boolean> safety =
        register(new BooleanSetting("Safety", false));
    protected final Setting<Double> minSelf =
        register(new NumberSetting<>("Min-Self", 2.0, 0.0, 6.0));
    protected final Setting<Boolean> waitForHoleLeave =
        register(new BooleanSetting("WaitForHoleLeave", false));
    protected final Setting<Boolean> offhand =
        register(new BooleanSetting("Offhand", false));
    protected final Setting<Boolean> requireOffhand =
        register(new BooleanSetting("RequireOffhand", false));

    protected List<BlockPos> safes   = Collections.emptyList();
    protected List<BlockPos> unsafes = Collections.emptyList();
    protected List<BlockPos> longs   = Collections.emptyList();
    protected List<BlockPos> bigs    = Collections.emptyList();

    protected final StopWatch disableTimer = new StopWatch();
    protected final StopWatch calcTimer = new StopWatch();
    protected OffhandMode offhandMode;
    protected EntityPlayer target;
    protected boolean waiting;

    public HoleFiller()
    {
        super("HoleFiller", Category.Combat);
        this.listeners.clear(); // Remove DisablingModule listeners
        this.listeners.add(this.listener);
        this.setData(new HoleFillerData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        if (target != null)
        {
            return (waiting ? TextColor.RED : "") + target.getName();
        }

        return null;
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();
        disableTimer.reset();
        calcTimer.setTime(0);
        target = null;
        offhandMode = null;
        waiting = false;
    }

    @Override
    protected void onDisable()
    {
        OffhandMode offhandMode = this.offhandMode;
        if (offhand.getValue()
            && offhandMode != null
            && OFFHAND.isPresent()
            && OFFHAND.get().getMode() == OffhandMode.OBSIDIAN)
        {
            OFFHAND.get().setMode(offhandMode);
        }
    }

    @Override
    public boolean execute()
    {
        if (offhand.getValue()
            && OFFHAND.isEnabled())
        {
            OffhandMode mode = offhandMode;
            if (packets.isEmpty()
                && mode != null
                && OFFHAND.get().getMode() == OffhandMode.OBSIDIAN)
            {
                OFFHAND.get().setMode(mode);
                offhandMode = null;
            }
            else if (!packets.isEmpty()
                && OFFHAND.get().getMode() != OffhandMode.OBSIDIAN
                && OFFHAND.get().isSafe())
            {
                offhandMode = OFFHAND.get().getMode();
                OFFHAND.get().setMode(OffhandMode.OBSIDIAN);
                OFFHAND.get().doOffhand();
            }

            if (requireOffhand.getValue()
                && !InventoryUtil.isHolding(Blocks.OBSIDIAN))
            {
                return false;
            }
        }

        return super.execute();
    }

    @Override
    public double getRange()
    {
        return range.getValue();
    }

    @Override
    public int getSafeHoles()
    {
        // TODO: this isn't perfect yet since we basically want to sort holes
        //  towards a player when using Smart Mode. Maybe an extra HoleManager?
        return 20;
    }

    @Override
    public int getUnsafeHoles()
    {
        return 20;
    }

    @Override
    public int get2x1Holes()
    {
        return longHoles.getValue() ? 4 : 0;
    }

    @Override
    public int get2x2Holes()
    {
        return bigHoles.getValue() ? 1 : 0;
    }

    @Override
    public void setSafe(List<BlockPos> safe)
    {
        this.safes = safe;
    }

    @Override
    public void setUnsafe(List<BlockPos> unsafe)
    {
        this.unsafes = unsafe;
    }

    @Override
    public void setLongHoles(List<BlockPos> longHoles)
    {
        this.longs = longHoles;
    }

    @Override
    public void setBigHoles(List<BlockPos> bigHoles)
    {
        this.bigs = bigHoles;
    }

    @Override
    public void setFinished()
    {
        /* NOOP */
    }

    @Override
    protected ListenerObby createListener()
    {
        return new ListenerObby(this);
    }

}
