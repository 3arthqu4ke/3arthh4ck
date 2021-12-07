package me.earth.earthhack.impl.modules.combat.webaura;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autotrap.modes.TrapTarget;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.helpers.blocks.noattack.NoAttackObbyListenerModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class WebAura extends NoAttackObbyListenerModule<ListenerWebAura>
{
    protected static final ModuleCache<Freecam> FREECAM =
            Caches.getModule(Freecam.class);

    protected final Setting<Double> placeRange =
            register(new NumberSetting<>("PlaceRange", 6.0, 0.1, 7.5));
    protected final Setting<TrapTarget> target =
            register(new EnumSetting<>("Target", TrapTarget.Closest));
    protected final Setting<Boolean> antiSelfWeb =
            register(new BooleanSetting("AntiSelfWeb", true));
    protected final Setting<Double> targetRange =
            register(new NumberSetting<>("Target-Range", 6.0, 0.1, 10.0));

    protected EntityPlayer currentTarget;

    public WebAura()
    {
        super("WebAura", Category.Combat);
        this.unregister(this.blockingType);
        this.setData(new WebAuraData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return target.getValue() == TrapTarget.Closest
                && currentTarget != null
                ? currentTarget.getName()
                : null;
    }

    @Override
    protected ListenerWebAura createListener()
    {
        return new ListenerWebAura(this);
    }

    @Override
    public boolean entityCheck(BlockPos pos)
    {
        return selfWebCheck(pos);
    }

    @Override
    protected boolean quickEntityCheck(BlockPos pos)
    {
        return !selfWebCheck(pos);
    }

    @Override
    public EntityPlayer getPlayerForRotations()
    {
        if (FREECAM.isEnabled())
        {
            EntityPlayer target = FREECAM.get().getPlayer();
            if (target != null)
            {
                return target;
            }
        }

        return mc.player;
    }

    protected boolean selfWebCheck(BlockPos pos)
    {
        return BlockUtil.getDistanceSq(pos)
                <= MathUtil.square(placeRange.getValue())
                    && (!antiSelfWeb.getValue()
                        || !getPlayerForRotations()
                                .getEntityBoundingBox()
                                .intersects(new AxisAlignedBB(pos)));
    }

}
