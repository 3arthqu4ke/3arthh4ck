package me.earth.earthhack.impl.modules.combat.anvilaura;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilMode;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilStage;
import me.earth.earthhack.impl.modules.combat.anvilaura.util.AnvilResult;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO: when mining we need 5 ticks delay
public class AnvilAura extends ObbyListenerModule<ListenerAnvilAura>
{
    protected final Setting<AnvilMode> mode =
        register(new EnumSetting<>("Mode", AnvilMode.Mine));
    protected final Setting<Integer> fastDelay =
        register(new NumberSetting<>("Fast-Delay", 0, 0, 1000))
            .setComplexity(Complexity.Medium);
    protected final Setting<Double> range =
        register(new NumberSetting<>("Range", 5.25, 0.1, 6.0));
    protected final Setting<Boolean> holdingAnvil =
        register(new BooleanSetting("HoldingAnvil", false));
    protected final Setting<Integer> yHeight =
        register(new NumberSetting<>("Y-Offset", 3, 0, 256))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> trap =
        register(new BooleanSetting("Trap", true))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> mineESP =
        register(new BooleanSetting("Mine-ESP", true))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> renderBest =
        register(new BooleanSetting("RenderBest", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> checkFalling =
        register(new BooleanSetting("CheckFalling", true))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> pressureFalling =
        register(new BooleanSetting("PressureFalling", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Integer> helpingBlocks =
        register(new NumberSetting<>("HelpingBlocks", 6, 0, 12))
            .setComplexity(Complexity.Expert);
    protected final Setting<Integer> trapHelping =
        register(new NumberSetting<>("Trap-Helping", 2, 0, 3))
            .setComplexity(Complexity.Expert);
    protected final Setting<Double> mineRange =
        register(new NumberSetting<>("Mine-Range", 6.0, 0.1, 10.0))
            .setComplexity(Complexity.Medium);
    public final ColorSetting box =
        register(new ColorSetting("Box", new Color(100, 100, 100, 155)));
    public final ColorSetting outline =
        register(new ColorSetting("Outline", new Color(0, 0, 0, 0)));
    public final Setting<Float> lineWidth =
        register(new NumberSetting<>("LineWidth", 1.5f, 0.0f, 10.0f))
            .setComplexity(Complexity.Expert);
    protected final Setting<Integer> mineTime =
        register(new NumberSetting<>("Mine-Time", 250, 0, 1000))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> confirmMine =
        register(new BooleanSetting("ConfirmMine", true))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> pressurePass =
        register(new BooleanSetting("PressurePass", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> crystal =
        register(new BooleanSetting("Crystal", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Integer> crystalDelay =
        register(new NumberSetting<>("CrystalDelay", 500, 0, 1000))
            .setComplexity(Complexity.Expert);

    protected List<AxisAlignedBB> renderBBs = Collections.emptyList();
    protected final AtomicBoolean awaiting = new AtomicBoolean();
    protected final StopWatch renderTimer  = new StopWatch();
    protected final StopWatch mineTimer    = new StopWatch();
    protected final StopWatch awaitTimer   = new StopWatch();
    protected final StopWatch crystalTimer = new StopWatch();
    protected AnvilStage stage = AnvilStage.ANVIL;
    protected AnvilResult currentResult;
    protected EntityPlayer target;
    protected AxisAlignedBB mineBB;
    protected Runnable action;

    protected int pressureSlot;
    protected int crystalSlot;
    protected int pickSlot;
    protected int obbySlot;

    protected BlockPos awaitPos;
    protected BlockPos minePos;
    protected EnumFacing mineFacing;

    public AnvilAura()
    {
        super("AnvilAura", Category.Combat);
        this.listeners.clear(); // Remove DisablingModule listeners
        this.listeners.add(this.listener);
        this.listeners.add(new ListenerRender(this));
        super.delay.setValue(500);
    }

    @Override
    protected boolean checkNull()
    {
        renderBBs = Collections.emptyList();
        mineBB = null;
        packets.clear();
        blocksPlaced = 0;

        if (mc.player == null || mc.world == null)
        {
            if (!holdingAnvil.getValue() && mode.getValue() != AnvilMode.Render)
            {
                this.disable();
            }

            return false;
        }

        return true;
    }

    @Override
    public String getDisplayInfo()
    {
        if (renderTimer.passed(600))
        {
            currentResult = null;
            target = null;
        }

        return target != null ? target.getName() : null;
    }

    @Override
    public boolean execute()
    {
        switch (stage)
        {
            case OBSIDIAN:
                if (obbySlot == -1)
                {
                    return false;
                }

                slot = obbySlot;
                break;
            case PRESSURE:
                if (pressureSlot == -1)
                {
                    return false;
                }

                slot = pressureSlot;
                break;
            case CRYSTAL:
                if (crystalSlot == -1)
                {
                    return false;
                }

                slot = crystalSlot;
            default:
        }

        if (action != null)
        {
            action.run();
            return true;
        }

        return super.execute();
    }

    @Override
    protected ListenerAnvilAura createListener()
    {
        return new ListenerAnvilAura(this, 500);
    }

    @Override
    protected boolean entityCheckSimple(BlockPos pos)
    {
        if (stage == AnvilStage.PRESSURE)
        {
            return true;
        }

        return super.entityCheckSimple(pos);
    }

    @Override
    public boolean entityCheck(BlockPos pos)
    {
        if (stage == AnvilStage.PRESSURE)
        {
            return true;
        }

        return super.entityCheck(pos);
    }

    @Override
    protected boolean quickEntityCheck(BlockPos pos)
    {
        if (stage == AnvilStage.PRESSURE)
        {
            return false;
        }

        return super.quickEntityCheck(pos);
    }

    @Override
    public int getDelay()
    {
        AnvilResult r = currentResult;
        if (r != null && r.hasSpecialPressure())
        {
            return fastDelay.getValue();
        }

        return super.getDelay();
    }

    public void setCurrentResult(AnvilResult result)
    {
        this.renderTimer.reset();
        this.currentResult = result;
        this.target = result.getPlayer();
    }

    public boolean isMining()
    {
        return mode.getValue() == AnvilMode.Mine
                && (!holdingAnvil.getValue()
                    || InventoryUtil.isHolding(Blocks.ANVIL));
    }

}
