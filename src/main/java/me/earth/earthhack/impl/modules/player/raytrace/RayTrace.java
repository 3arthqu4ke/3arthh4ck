package me.earth.earthhack.impl.modules.player.raytrace;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.modules.player.liquids.LiquidInteract;

public class RayTrace extends Module
{
    private static final ModuleCache<Phase> PHASE =
            Caches.getModule(Phase.class);
    private static final ModuleCache<LiquidInteract> LIQUID_INTERACT =
            Caches.getModule(LiquidInteract.class);

    protected final Setting<Boolean> phase =
        register(new BooleanSetting("Phase", true));
    protected final Setting<Boolean> liquids =
        register(new BooleanSetting("Liquids", true));
    protected final Setting<Boolean> liquidCrystalPlace =
        register(new BooleanSetting("Liquid-CrystalPlace", true));
    protected final Setting<Boolean> onlyPhase =
        register(new BooleanSetting("OnlyPhase", true));

    public RayTrace()
    {
        super("PhaseTrace", Category.Player);
    }

    public boolean isActive()
    {
        return this.isEnabled()
            && (phase.getValue() && PHASE.isEnabled()
                || LIQUID_INTERACT.isEnabled() && liquids.getValue());
    }

    public boolean phaseCheck()
    {
        return onlyPhase.getValue()
                && PHASE.isEnabled()
                && PHASE.get().isPhasing();
    }

    public boolean liquidCrystalPlace()
    {
        return LIQUID_INTERACT.isEnabled()
                && liquidCrystalPlace.getValue()
                && this.isEnabled();
    }


}
