package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.addable.BlockAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.blocks.attack.InstantAttackListener;
import me.earth.earthhack.impl.util.helpers.blocks.attack.InstantAttackingModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.math.Passable;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class Scaffold extends BlockAddingModule
        implements InstantAttackingModule
{
    protected final Setting<Boolean> tower  =
            register(new BooleanSetting("Tower", true));
    protected final Setting<Boolean> down   =
            register(new BooleanSetting("Down", false));
    protected final Setting<Boolean> offset =
            register(new BooleanSetting("Offset", true));
    protected final Setting<Boolean> rotate =
            register(new BooleanSetting("Rotate", true));
    protected final Setting<Integer> keepRotations =
            register(new NumberSetting<>("Keep-Rotations", 0, 0, 500));
    protected final Setting<Integer> preRotate =
            register(new NumberSetting<>("Pre-Rotations", 0, 0, 500));
    protected final Setting<Boolean> aac =
            register(new BooleanSetting("AAC", false));
    protected final Setting<Integer> aacDelay =
            register(new NumberSetting<>("AAC-Delay", 150, 0, 1000));
    protected final Setting<Boolean> stopSprint =
            register(new BooleanSetting("StopSprint", false));
    protected final Setting<Boolean> fastSneak =
            register(new BooleanSetting("FastDown", false));
    protected final Setting<Boolean> helping  =
            register(new BooleanSetting("Helping", false));
    protected final Setting<Boolean> raytrace  =
            register(new BooleanSetting("Raytrace", false));
    protected final Setting<Boolean> swing  =
            register(new BooleanSetting("Swing", false));
    protected final Setting<Boolean> checkState =
            register(new BooleanSetting("CheckState", true));
    protected final Setting<Boolean> smartSneak =
            register(new BooleanSetting("Smart-Sneak", true));
    protected final Setting<Boolean> attack =
            register(new BooleanSetting("Attack", false));
    protected final Setting<Boolean> instant  =
            register(new BooleanSetting("Instant", true));
    // TODO: place-async
    protected final Setting<Pop> pop =
            register(new EnumSetting<>("Pop", Pop.None));
    protected final Setting<Integer> popTime =
            register(new NumberSetting<>("Pop-Time", 500, 0, 500));
    protected final Setting<Integer> cooldown =
            register(new NumberSetting<>("Cooldown", 500, 0, 500));
    protected final Setting<Integer> breakDelay =
            register(new NumberSetting<>("BreakDelay", 250, 0, 500));
    protected final Setting<Boolean> freecam =
            register(new BooleanSetting("Freecam", false));
    protected final Setting<Boolean> spectate =
            register(new BooleanSetting("Spectate", false));

    protected final StopWatch rotationTimer = new StopWatch();
    protected final StopWatch breakTimer = new StopWatch();
    protected final StopWatch towerTimer = new StopWatch();
    protected final StopWatch aacTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected float[] rotations;
    protected EnumFacing facing;
    protected Entity crystal;
    protected BlockPos pos;
    protected BlockPos rot;

    public Scaffold()
    {
        super("Scaffold",
                Category.Player,
                s -> "Black/Whitelist " + s.getName() + " from Scaffolding.");
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerPush(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new InstantAttackListener<>(this));
        super.listType.setValue(ListType.BlackList);
    }

    @Override
    protected void onEnable()
    {
        towerTimer.reset();
        pos    = null;
        facing = null;
        rot    = null;
    }

    protected BlockPos findNextPos()
    {
        BlockPos underPos = new BlockPos(mc.player).down();
        boolean under = false;
        if (down.getValue()
            && !mc.gameSettings.keyBindJump.isKeyDown()
            && mc.gameSettings.keyBindSneak.isKeyDown())
        {
            under = true;
            underPos = underPos.down();
        }

        if (mc.world.getBlockState(underPos).getMaterial().isReplaceable())
        {
            if (!under || mc.world.getBlockState(underPos.up())
                                  .getMaterial()
                                  .isReplaceable())
            {
                return underPos;
            }
        }

        if (!offset.getValue())
        {
            return null;
        }

        if (mc.gameSettings.keyBindForward.isKeyDown()
                && !mc.gameSettings.keyBindBack.isKeyDown())
        {
            BlockPos forwardPos = underPos.offset(
                    mc.player.getHorizontalFacing());

            if (mc.world.getBlockState(forwardPos)
                        .getMaterial()
                        .isReplaceable())
            {
                return forwardPos;
            }
        }
        else if (mc.gameSettings.keyBindBack.isKeyDown()
                && !mc.gameSettings.keyBindForward.isKeyDown())
        {
            BlockPos backPos = underPos.offset(
                    mc.player.getHorizontalFacing().getOpposite());

            if (mc.world.getBlockState(backPos).getMaterial().isReplaceable())
            {
                return backPos;
            }
        }

        if (mc.gameSettings.keyBindRight.isKeyDown()
                && !mc.gameSettings.keyBindLeft.isKeyDown())
        {
            BlockPos rightPos = underPos.offset(
                    mc.player.getHorizontalFacing().rotateY());

            if (mc.world.getBlockState(rightPos).getMaterial().isReplaceable())
            {
                return rightPos;
            }
        }
        else if (mc.gameSettings.keyBindLeft.isKeyDown()
                && !mc.gameSettings.keyBindRight.isKeyDown())
        {
            BlockPos leftPos = underPos.offset(
                    mc.player.getHorizontalFacing().rotateYCCW());

            if (mc.world.getBlockState(leftPos).getMaterial().isReplaceable())
            {
                return leftPos;
            }
        }

        return null;
    }

    @Override
    public Pop getPop()
    {
        return pop.getValue();
    }

    @Override
    public int getPopTime()
    {
        return popTime.getValue();
    }

    @Override
    public double getRange()
    {
        return 6.0;
    }

    @Override
    public double getTrace()
    {
        return 3.0;
    }

    @Override
    public boolean shouldAttack(EntityEnderCrystal entity)
    {
        if (!attack.getValue()
            || !instant.getValue()
            || MovementUtil.noMovementKeys()
                && !mc.player.movementInput.jump)
        {
            return false;
        }

        BlockPos pos = this.pos;
        if (pos != null)
        {
            return entity.getEntityBoundingBox().intersects(
                    new AxisAlignedBB(pos));
        }

        return false;
    }

    @Override
    public Passable getTimer()
    {
        return breakTimer;
    }

    @Override
    public int getBreakDelay()
    {
        return breakDelay.getValue();
    }

    @Override
    public int getCooldown()
    {
        return cooldown.getValue();
    }

}
