package me.earth.earthhack.impl.modules.combat.antitrap;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.combat.antitrap.util.AntiTrapMode;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: INSTANT!!!!
public class AntiTrap extends ObbyModule
{
    protected final Setting<AntiTrapMode> mode =
        registerBefore(new EnumSetting<>("Mode", AntiTrapMode.Crystal), blocks);
    protected final Setting<Boolean> offhand =
        register(new BooleanSetting("Offhand", false));
    protected final Setting<Integer> timeOut =
        register(new NumberSetting<>("TimeOut", 400, 0, 1000));
    protected final Setting<Boolean> empty   =
        register(new BooleanSetting("Empty", true));
    protected final Setting<Boolean> swing   =
        register(new BooleanSetting("Swing", false));
    protected final Setting<Boolean> highFill =
        register(new BooleanSetting("HighFill", false));
    protected final Setting<Integer> confirm =
        register(new NumberSetting<>("Confirm", 250, 0, 1000));
    protected final Setting<Boolean> autoOff =
        register(new BooleanSetting("Auto-Off", true));

    /** Blocks that have been placed an await a SPacketBlockChange */
    protected final Map<BlockPos, Long> placed = new HashMap<>();
    /** Positions that have been confirmed by a SPacketBlockChange */
    protected final Set<BlockPos> confirmed = new HashSet<>();
    /** Manages the {@link AntiTrap#timeOut}. */
    protected final StopWatch interval = new StopWatch();
    protected RayTraceResult result;
    protected OffhandMode previous;
    protected BlockPos startPos;
    protected BlockPos pos;

    public AntiTrap()
    {
        super("AntiTrap", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.setData(new AntiTrapData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().name();
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();
        previous = null;
        placed.clear();
        confirmed.clear();
        if (super.checkNull() && interval.passed(timeOut.getValue()))
        {
            interval.reset();
            result = null;
            pos    = null;
            startPos = PositionUtil.getPosition();
        }
        else
        {
            this.disable();
        }
    }

    @Override
    protected void onDisable()
    {
        if (offhand.getValue() && previous != null)
        {
            ListenerMotion.OFFHAND.computeIfPresent(o -> o.setMode(previous));
        }
    }

    @Override
    public boolean placeBlock(BlockPos pos)
    {
        boolean hasPlaced = super.placeBlock(pos);
        if (hasPlaced)
        {
            placed.put(pos, System.currentTimeMillis());
        }

        return hasPlaced;
    }

    protected List<BlockPos> getCrystalPositions()
    {
        List<BlockPos> result = new ArrayList<>();
        BlockPos playerPos = PositionUtil.getPosition();
        if (!mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class,
                    new AxisAlignedBB(playerPos, playerPos.up().add(1, 2, 1)))
               .isEmpty())
        {
            this.disable();
            return result;
        }

        for (Vec3i vec : AntiTrapMode.Crystal.getOffsets())
        {
            BlockPos pos = playerPos.add(vec);
            if (BlockUtil.canPlaceCrystal(pos, false, false))
            {
                result.add(pos);
            }
        }

        return result;
    }

}
