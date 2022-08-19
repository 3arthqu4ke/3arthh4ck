package me.earth.earthhack.impl.modules.misc.nuker;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Queue;
import java.util.*;

public class Nuker extends Module
{
    protected final Setting<Boolean> nuke =
            register(new BooleanSetting("Nuke", true));
    protected final Setting<Integer> blocks =
            register(new NumberSetting<>("Blocks/Attack", 1, 1, 50));
    protected final Setting<Integer> delay  =
            register(new NumberSetting<>("Click-Delay", 25, 0, 250));
    protected final Setting<Rotate> rotate  =
            register(new EnumSetting<>("Rotations", Rotate.None));
    protected final Setting<Integer> width =
            register(new NumberSetting<>("Selection-W", 1, 1, 6));
    protected final Setting<Integer> height =
            register(new NumberSetting<>("Selection-H", 1, 1, 6));
    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 6.0f, 0.1f, 6.0f));
    protected final Setting<Boolean> render =
            register(new BooleanSetting("Render", true));
    protected final Setting<Color> color =
            register(new ColorSetting("Color", new Color(255, 255, 255, 125)));
    protected final Setting<Boolean> shulkers =
            register(new BooleanSetting("Shulkers", false));
    protected final Setting<Boolean> hoppers =
            register(new BooleanSetting("Hoppers", false));
    protected final Setting<Boolean> instant =
            register(new BooleanSetting("Predict", false));
    protected final Setting<Swing> swing =
            register(new EnumSetting<>("Swing", Swing.Packet));
    protected final Setting<Boolean> speedMine = //TODO: make normal work!
            register(new BooleanSetting("Packet", true));
    protected final Setting<Boolean> autoTool =
            register(new BooleanSetting("AutoTool", true));
    protected final Setting<Integer> timeout =
            register(new NumberSetting<>("Delay", 100, 50, 500));

    protected final Queue<Runnable> actions = new LinkedList<>();
    protected final StopWatch timer = new StopWatch();
    protected Set<BlockPos> currentSelection;
    protected float[] rotations;
    protected boolean breaking;
    protected int lastSlot;

    public Nuker()
    {
        super("Nuker", Category.Misc);
        this.listeners.add(new ListenerClickBlock(this));
        this.listeners.add(new ListenerMultiChange(this));
        this.listeners.add(new ListenerChange(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerRender(this));
        this.setData(new NukerData(this));
    }

    @Override
    protected void onEnable()
    {
        currentSelection = null;
        rotations        = null;
        breaking         = false;

        actions.clear();
    }

    /**
     * @return Blocks that we want to break when Hopper
     *         or Shulker is enabled.
     */
    protected Set<Block> getBlocks()
    {
        Set<Block> result = new HashSet<>();

        if (hoppers.getValue())
        {
            result.add(Blocks.HOPPER);
        }

        if (shulkers.getValue())
        {
            result.addAll(SpecialBlocks.SHULKERS);
        }

        return result;
    }

    /**
     * Breaks the given BlockPositions. If we need to
     * rotate all actions will be added to {@link Nuker#actions},
     * which will be run after onUpdateWalkingPlayer.
     *
     * @param selection the blocks to break.
     */
    protected void breakSelection(Set<BlockPos> selection, boolean autoTool)
    {
        int i = 1;
        lastSlot = -1;
        Set<BlockPos> toRemove = new HashSet<>();
        for (BlockPos pos : selection)
        {
            if (!MineUtil.canBreak(pos))
            {
                toRemove.add(pos);
                continue;
            }

            RayTraceResult result;
            float[] rotations;
            if (rotate.getValue() != Rotate.None)
            {
                rotations = RotationUtil.getRotationsToTopMiddle(pos.up());
                result = RayTraceUtil.getRayTraceResult(
                            rotations[0],
                            rotations[1],
                            range.getValue());
            }
            else
            {
                rotations = null;
                result =
                        new RayTraceResult(new Vec3d(0.5, 1.0, 0.5),
                                EnumFacing.UP);
            }

            if (rotations != null)
            {
                if (this.rotations == null)
                {
                    this.rotations = rotations;
                }
                else
                {
                    actions.add(() ->
                            mc.player.connection.sendPacket(
                                    new CPacketPlayer.Rotation(
                                            rotations[0],
                                            rotations[1],
                                            mc.player.onGround)));
                }
            }

            if (rotate.getValue() == Rotate.None)
            {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                {
                    if (autoTool)
                    {
                        if (lastSlot == -1)
                        {
                            lastSlot = mc.player.inventory.currentItem;
                        }

                        InventoryUtil.switchTo(MineUtil.findBestTool(pos));
                    }

                    if (speedMine.getValue())
                    {
                        mc.player
                          .connection
                          .sendPacket(getPacket(pos, result.sideHit, true));
                        mc.player
                          .connection
                          .sendPacket(getPacket(pos, result.sideHit, false));
                    }
                    else
                    {
                        mc.playerController
                          .onPlayerDamageBlock(pos, result.sideHit);
                    }

                    swing.getValue().swing(EnumHand.MAIN_HAND);
                });
            }
            else
            {
                if (autoTool)
                {
                    actions.add(() ->
                    {
                        if (lastSlot == -1)
                        {
                            lastSlot = mc.player.inventory.currentItem;
                        }

                        InventoryUtil.switchTo(MineUtil.findBestTool(pos));
                    });
                }

                if (speedMine.getValue())
                {
                    actions.add(() ->
                    {
                        mc.player.connection
                            .sendPacket(getPacket(pos, result.sideHit, true));
                        mc.player.connection
                            .sendPacket(getPacket(pos, result.sideHit, false));
                    });
                }
                else
                {
                    actions.add(() -> mc.playerController
                            .onPlayerDamageBlock(pos, result.sideHit));
                }

                actions.add(() -> swing.getValue().swing(EnumHand.MAIN_HAND));
            }

            toRemove.add(pos);

            if (i >= blocks.getValue()
                    || rotate.getValue() == Rotate.Normal)
            {
                break;
            }

            i++;
        }

        selection.removeAll(toRemove);

        if (!actions.isEmpty())
        {
            if (autoTool)
            {
                InventoryUtil.switchTo(lastSlot);
            }

            timer.reset();
        }
    }

    protected void attack(BlockPos pos)
    {
        RayTraceResult result;
        float[] rotations = RotationUtil.getRotationsToTopMiddle(pos.up());
        result = RayTraceUtil.getRayTraceResult(
                rotations[0],
                rotations[1],
                range.getValue());

        if (rotate.getValue() == Rotate.Packet)
        {
            PingBypass.sendToActualServer(
                    new CPacketPlayer
                            .Rotation(rotations[0],
                            rotations[1],
                            mc.player.onGround));
        }

        mc.player.connection.sendPacket(
                getPacket(pos, result.sideHit, true));
        mc.player.connection.sendPacket(
                getPacket(pos, result.sideHit, false));
    }

    protected Packet<?> getPacket(BlockPos pos,
                                  EnumFacing facing,
                                  boolean start)
    {
        if (start)
        {
            return new CPacketPlayerDigging(CPacketPlayerDigging
                    .Action
                    .START_DESTROY_BLOCK,
                    pos,
                    facing);
        }

        return new CPacketPlayerDigging(CPacketPlayerDigging
                .Action
                .STOP_DESTROY_BLOCK,
                pos,
                facing);
    }

    public Set<BlockPos> getSelection(BlockPos pos)
    {
        Set<BlockPos> result = new LinkedHashSet<>();
        result.add(pos);

        EnumFacing entityF = EnumFacing
                               .getDirectionFromEntityLiving(pos, mc.player)
                               .getOpposite();

        EnumFacing horizontal = mc.player.getHorizontalFacing();

        for (int i = 1; i < width.getValue(); i++)
        {
            EnumFacing facing = getFacing(i, entityF, false, horizontal);

            BlockPos w = pos.offset(facing);
            while (result.contains(w))
            {
                w = w.offset(facing);
            }

            if (MineUtil.canBreak(w)
                    && BlockUtil.getDistanceSqDigging(mc.player, w)
                            <= MathUtil.square(range.getValue()))
            {
                result.add(w);
            }
        }

        Set<BlockPos> added = new LinkedHashSet<>(result);
        for (int i = 1; i < height.getValue(); i++)
        {
            EnumFacing facing = getFacing(i, entityF, true, horizontal);

            for (BlockPos p : result)
            {
                BlockPos h = p.offset(facing);
                while (added.contains(h))
                {
                    h = h.offset(facing);
                }

                if (MineUtil.canBreak(h)
                    && BlockUtil.getDistanceSqDigging(mc.player, h)
                            <= MathUtil.square(range.getValue())
                    && (entityF == EnumFacing.DOWN
                        || mc.player.posY < pos.getY()))
                {
                    added.add(h);
                }
            }
        }

        return added;
    }

    private EnumFacing getFacing(int index,
                                 EnumFacing entityFacing,
                                 boolean h,
                                 EnumFacing horizontal)
    {
        if (entityFacing == EnumFacing.UP || entityFacing == EnumFacing.DOWN)
        {
            if (h)
            {
                return index % 2 == 0 ? horizontal.getOpposite() : horizontal;
            }

            EnumFacing result = get2ndHorizontalOpposite(horizontal);
            return index % 2 == 0 ? result.getOpposite() : result;
        }

        if (h)
        {
            return index % 2 == 0 ? EnumFacing.UP : EnumFacing.DOWN;
        }

        EnumFacing result = get2ndHorizontalOpposite(horizontal);
        return index % 2 == 0 ? result.getOpposite() : result;
    }

    private EnumFacing get2ndHorizontalOpposite(EnumFacing facing)
    {
        for (EnumFacing f : EnumFacing.values())
        {
            if (f == facing
                    || f.getOpposite() == facing
                    || f == EnumFacing.UP
                    || f == EnumFacing.DOWN)
            {
                continue;
            }

            return f;
        }

        return EnumFacing.UP;
    }

}
