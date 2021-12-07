package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.mcf.MCF;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class MiddleClickPearl extends Module
{
    private static final ModuleCache<MCF> MCFRIENDS =
            Caches.getModule(MCF.class);

    protected final Setting<Boolean> preferMCF   =
            register(new BooleanSetting("PrioMCF", false));
    protected final Setting<Boolean> cancelMCF   =
            register(new BooleanSetting("CancelMCF", true));
    protected final Setting<Boolean> cancelBlock =
            register(new BooleanSetting("CancelBlock", false));

    protected Runnable runnable;

    public MiddleClickPearl()
    {
        super("MCP", Category.Player);
        this.listeners.add(new ListenerMiddleClick(this));
        this.listeners.add(new ListenerMotion(this));
    }

    @Override
    public void onEnable()
    {
        runnable = null;
    }

    @Override
    protected void onDisable()
    {
        runnable = null;
    }

    protected boolean prioritizeMCF()
    {
        return preferMCF.getValue() && MCFRIENDS.isEnabled()
                && mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY
                && mc.objectMouseOver.entityHit instanceof EntityPlayer;
    }

}
