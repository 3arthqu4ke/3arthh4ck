package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.surround.modes.Movement;
import me.earth.earthhack.impl.modules.combat.surround.modes.SurroundFreecamMode;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

// TODO: Make City thing better
public class Surround extends ObbyModule
{
    protected static final ModuleCache<Freecam> FREECAM =
        Caches.getModule(Freecam.class);
    protected static final ModuleCache<BlockLag> BLOCKLAG =
        Caches.getModule(BlockLag.class);

    protected final Setting<Boolean> center    =
        register(new BooleanSetting("Center", true));
    protected final Setting<Movement> movement =
        register(new EnumSetting<>("Movement", Movement.Static));
    protected final Setting<Float> speed       =
        register(new NumberSetting<>("Speed", 19.5f, 0.0f, 35.0f));
    protected final Setting<Boolean> noTrap    =
        register(new BooleanSetting("NoTrap", false));
    protected final Setting<Boolean> floor     =
        register(new BooleanSetting("Floor", false));
    protected final Setting<Integer> extend    =
        register(new NumberSetting<>("Extend", 1, 1, 3));
    protected final Setting<Integer> eDelay    =
        register(new NumberSetting<>("E-Delay", 100, 0, 1000));
    protected final Setting<Boolean> holeC     =
        register(new BooleanSetting("Hole-C", false));
    protected final Setting<Boolean> instant   =
        register(new BooleanSetting("Instant", false));
    protected final Setting<Boolean> sound =
        register(new BooleanSetting("Sound", false));
    protected final Setting<Integer> playerExtend =
        register(new NumberSetting<>("PlayerExtend", 0, 0, 4));
    protected final Setting<Boolean> peNoTrap =
        register(new BooleanSetting("PE-NoTrap", false));
    protected final Setting<Boolean> noTrapBlock =
        register(new BooleanSetting("NoTrapBlock", false));
    protected final Setting<Boolean> multiTrap =
        register(new BooleanSetting("MultiTrap", false));
    protected final Setting<Boolean> trapExtend =
        register(new BooleanSetting("TrapExtend", false));
    protected final Setting<Boolean> newVer =
        register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> deltaY =
        register(new BooleanSetting("Delta-Y", true));
    protected final Setting<Boolean> centerY =
        register(new BooleanSetting("Center-Y", false));
    protected final Setting<Boolean> predict =
        register(new BooleanSetting("Predict", false));
    protected final Setting<Boolean> async =
        register(new BooleanSetting("Async", false));
    protected final Setting<Boolean> resync =
        register(new BooleanSetting("Resync", false));
    protected final Setting<Boolean> crystalCheck =
        register(new BooleanSetting("Crystal-Check", true));
    protected final Setting<Boolean> burrow =
        register(new BooleanSetting("Burrow", false));
    protected final Setting<Boolean> noSelfExtend =
        register(new BooleanSetting("NoSelfExtend", false));
    protected final Setting<SurroundFreecamMode> freecam =
        register(new EnumSetting<>("Freecam", SurroundFreecamMode.Off));
    public final Setting<Boolean> teleport =
        register(new BooleanSetting("Teleport", false));
    protected final Setting<Double> yTeleportRange =
        register(new NumberSetting<>("Y-TeleportRange", 0.0, 0.0, 100.0));
    public final Setting<Boolean> fixStartPos =
        register(new BooleanSetting("FixStartPos", true))
            .setComplexity(Complexity.Dev);

    /** A Listener for the SetDeadManager (Instant + Sound). */
    protected final ListenerSound soundObserver = new ListenerSound(this);
    /** Handles the delay from enabling until we fully extend */
    protected final StopWatch extendingWatch = new StopWatch();
    /** The Positions surrounding us. */
    protected Set<BlockPos> targets = new HashSet<>();
    /** Blocks that have been placed and await a SPacketBlockChange */
    protected Set<BlockPos> placed = new HashSet<>();
    /** Positions that have been confirmed by a SPacketBlockChange */
    protected Set<BlockPos> confirmed = new HashSet<>();
    /** The position we were at when we enabled the module */
    public volatile BlockPos startPos;
    /** <tt>true</tt> if we are centered, or don't have to center */
    protected boolean setPosition;
    public boolean blockTeleporting;

    public Surround()
    {
        super("Surround", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerTeleport(this));
        this.setData(new SurroundData(this));
    }

    @Override
    protected void onEnable()
    {
        Managers.SET_DEAD.addObserver(soundObserver);
        super.onEnable();
        if (super.checkNull())
        {
            confirmed.clear();
            targets.clear();
            placed.clear();
            attacking    = null;
            setPosition  = false;
            startPos     = getPlayerPos();
            extendingWatch.reset();

            if (burrow.getValue() && !BLOCKLAG.isEnabled())
            {
                BLOCKLAG.toggle();
            }
        }
    }

    @Override
    protected void onDisable()
    {
        Managers.SET_DEAD.removeObserver(soundObserver);
    }

    /**
     * Centers the player. To not produce
     * any extra packets we need to wait until
     * after onUpdateWalkingPlayer.Post, so that
     * our position has been updated for the server,
     * before we send any place packets.
     */
    protected void center()
    {
        if (center.getValue()
            && !setPosition
            && startPos != null
            && mc.world.getBlockState(startPos).getBlock() != Blocks.WEB
            && (holeC.getValue() || !HoleUtil.isHole(startPos, false)[0]))
        {
            double x = startPos.getX() + 0.5;
            double y = centerY.getValue() ? startPos.getY() : getPlayer().posY;
            double z = startPos.getZ() + 0.5;
            getPlayer().setPosition(x, y, z);
            getPlayer().setVelocity(0.0, getPlayer().motionY, 0.0);
        }
        else
        {
            setPosition = true;
        }
    }

    /**
     * Runs {@link Surround#check()} and if we can place,
     * updates our surrounding blocks.
     *
     * @return <tt>true</tt> if we can proceed.
     */
    protected boolean updatePosAndBlocks()
    {
        if (check())
        {
            Set<BlockPos> blocked = createBlocked();
            Set<BlockPos> surrounding = createSurrounding(blocked,
                                                          mc.world
                                                              .playerEntities);
            placed.retainAll(surrounding);
            this.targets = surrounding;
            return true;
        }

        return false;
    }

    /**
     * Checks if FreeCam is enabled or we have no Obby/Echests,
     * we move too fast, or the timer hasn't passed the delay yet.
     * In all these cases return <tt>false</tt>.
     *
     * @return if we can proceed.
     */
    private boolean check()
    {
        if (FREECAM.isEnabled()
            && freecam.getValue() == SurroundFreecamMode.Off)
        {
            return false;
        }

        slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN,
                                             Blocks.ENDER_CHEST);
        if (slot == -1)
        {
            ModuleUtil.disable(this, TextColor.RED + "Disabled, no Obsidian.");
            return false;
        }

        if (FREECAM.isEnabled() && movement.getValue() != Movement.Static)
        {
            return timer.passed(delay.getValue());
        }

        double teleported;
        double yTeleportRange = MathUtil.square(this.yTeleportRange.getValue());
        switch (movement.getValue())
        {
            case None:
                break;
            case Static:
                BlockPos currentPos = getPlayerPos();
                if (!currentPos.equals(startPos))
                {
                    this.disable();
                    return false;
                }
                break;
            case Y:
                currentPos = getPlayerPos();
                BlockPos startPos = this.startPos;
                teleported = startPos == null
                    ? 0.0
                    : currentPos.distanceSq(startPos);

                if (startPos != null && startPos.getY() != currentPos.getY()
                    || yTeleportRange != 0.0 && yTeleportRange < teleported)
                {
                    this.disable();
                    return false;
                }
                else if (fixStartPos.getValue())
                {
                    this.startPos = currentPos;
                }

                break;
            case YPlus:
                currentPos = getPlayerPos();
                startPos = this.startPos;
                teleported = startPos == null
                    ? 0.0
                    : currentPos.distanceSq(startPos);

                if (startPos != null && startPos.getY() < currentPos.getY()
                    || yTeleportRange != 0.0 && yTeleportRange < teleported)
                {
                    this.disable();
                    return false;
                }
                else if (fixStartPos.getValue())
                {
                    this.startPos = currentPos;
                }

                break;
            case Limit:
                if (Managers.SPEED.getSpeed() > speed.getValue())
                {
                    return false;
                }
                break;
            case Disable:
                if (Managers.SPEED.getSpeed() > speed.getValue())
                {
                    this.disable();
                    return false;
                }
                break;
            default:
        }

        return timer.passed(delay.getValue());
    }

    /**
     * Places a block on the given position.
     * {@link BlockPlacingModule#placeBlock} will be called,
     * for the position and if needed for a helping position.
     * Returns <tt>true</tt> if the Block/Place limit has been
     * reached.
     *
     * @param pos the position.
     * @return if we cant place anymore.
     */
    @Override
    public boolean placeBlock(BlockPos pos)
    {
        boolean hasPlaced = super.placeBlock(pos);
        if (hasPlaced)
        {
            placed.add(pos);
        }

        return hasPlaced;
    }

    /**
     * Very interesting code that returns all
     * positions that have to be surrounded.
     *
     * @return positions to be surrounded.
     */
    public Set<BlockPos> createBlocked()
    {
        Set<BlockPos> blocked = new HashSet<>();
        BlockPos playerPos = getPlayerPos();
        if (HoleUtil.isHole(playerPos, false)[0]
            || center.getValue() // && !setPosition
            || extend.getValue() == 1
            || !extendingWatch.passed(eDelay.getValue()))
        {
            blocked.add(playerPos);
        }
        else
        {
            List<BlockPos> unfiltered =
                new ArrayList<>(PositionUtil.getBlockedPositions(getPlayer()))
                    .stream()
                    .sorted(Comparator.comparingDouble(pos ->
                                                           BlockUtil.getDistanceSq(getPlayer(), pos)))
                    .collect(Collectors.toList());

            List<BlockPos> filtered =
                new ArrayList<>(unfiltered)
                    .stream()
                    .filter(pos ->
                                mc.world
                                    .getBlockState(pos)
                                    .getMaterial()
                                    .isReplaceable()
                                    && mc.world
                                    .getBlockState(pos.up())
                                    .getMaterial()
                                    .isReplaceable())
                    .collect(Collectors.toList());

            if (extend.getValue() == 3
                && filtered.size() == 2
                && unfiltered.size() == 4)
            {
                // Prevents that a pos like this
                // (x == block, o == air, i = player):
                //
                //  o x                              x
                //  x i     can extend to this:    x o x
                //                                   x i
                //
                if (unfiltered.get(0).equals(filtered.get(0))
                    && unfiltered.get(3).equals(filtered.get(1)))
                {
                    filtered.clear();
                    filtered.add(playerPos);
                }
            }

            if (extend.getValue() == 2 && filtered.size() > 2
                || extend.getValue() == 3 && filtered.size() == 3)
            {
                // Prevents that a pos like this
                // (x == block, o == air, i = player):
                //
                //  x  o                               x
                //   i      can extend to this:    x   o  x
                //                               x   i    x
                //                                 x   x
                //
                // we should, unless we phased in, be able to place on o
                while (filtered.size() > 2)
                {
                    filtered.remove(filtered.size() - 1);
                }
            }

            blocked.addAll(filtered);
        }

        if (blocked.isEmpty())
        {
            blocked.add(playerPos);
        }

        return blocked;
    }

    protected boolean shouldInstant(boolean sound)
    {
        return instant.getValue()
            && rotate.getValue() != Rotate.Normal
            && (!sound || this.sound.getValue());
    }

    protected boolean isBlockingTrap(BlockPos pos, List<EntityPlayer> players)
    {
        if (mc.world.getBlockState(pos.up()).getMaterial().isReplaceable())
        {
            return false;
        }

        EnumFacing relative = getFacingRelativeToPlayer(pos, getPlayer());
        if (relative != null
            && !trapExtend.getValue()
            && BlockUtil.canPlaceCrystal(getPlayerPos()
                                             .down()
                                             .offset(relative, 2),
                                         true,
                                         newVer.getValue()))
        {
            return false;
        }

        for (EntityPlayer player : players)
        {
            if (player == null
                || getPlayer().equals(player)
                || EntityUtil.isDead(player)
                || Managers.FRIENDS.contains(player)
                || player.getDistanceSq(pos) > 9)
            {
                continue;
            }

            BlockPos playerPos = PositionUtil.getPosition(player);
            for (EnumFacing facing : EnumFacing.HORIZONTALS)
            {
                if (facing == relative
                    || facing.getOpposite() == relative
                    || !pos.offset(facing).equals(playerPos))
                {
                    continue;
                }

                if (BlockUtil.canPlaceCrystal(pos.offset(facing.getOpposite())
                                                 .down(),
                                              true,
                                              newVer.getValue()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected EnumFacing getFacingRelativeToPlayer(BlockPos pos,
                                                   EntityPlayer player)
    {
        double x = pos.getX() + 0.5 - player.posX;
        double z = pos.getZ() + 0.5 - player.posZ;
        int compare = Double.compare(Math.abs(x), Math.abs(z));
        if (compare == 0)
        {
            return null;
        }

        return compare < 0 ? z < 0
            ? EnumFacing.NORTH
            : EnumFacing.SOUTH
            : x < 0
            ? EnumFacing.WEST
            : EnumFacing.EAST;
    }

    public Set<BlockPos> createSurrounding(Set<BlockPos> blocked,
                                           List<EntityPlayer> players)
    {
        Set<BlockPos> surrounding = new HashSet<>();
        for (BlockPos pos : blocked)
        {
            for (EnumFacing facing : EnumFacing.HORIZONTALS)
            {
                BlockPos offset = pos.offset(facing);
                if (!blocked.contains(offset))
                {
                    surrounding.add(offset);
                    if (noTrap.getValue())
                    {
                        surrounding.add(offset.down());
                    }
                }
            }

            if (floor.getValue())
            {
                surrounding.add(pos.down());
            }
        }

        // TODO: Extend around webs?
        for (int i = 0; i < playerExtend.getValue(); i++)
        {
            Set<BlockPos> extendedPositions = new HashSet<>();
            Iterator<BlockPos> itr = surrounding.iterator();
            while (itr.hasNext())
            {
                BlockPos pos = itr.next();
                boolean remove = false;
                for (EntityPlayer player : players)
                {
                    if (player == null
                        || (noSelfExtend.getValue() && player == mc.player)
                        || PlayerUtil.isFakePlayer(player) // do we want this?
                        || EntityUtil.isDead(player)
                        || !BlockUtil.isBlocking(pos,
                                                 player,
                                                 blockingType.getValue()))
                    {
                        continue;
                    }

                    for (EnumFacing facing : EnumFacing.HORIZONTALS)
                    {
                        BlockPos offset = pos.offset(facing);
                        if (blocked.contains(offset))
                        {
                            continue;
                        }

                        remove = true;
                        extendedPositions.add(offset);
                        if (peNoTrap.getValue())
                        {
                            extendedPositions.add(offset.down());
                        }
                    }
                }

                if (remove)
                {
                    itr.remove();
                }
            }

            surrounding.addAll(extendedPositions);
        }

        if (noTrapBlock.getValue())
        {
            Set<BlockPos> trapBlocks =
                surrounding.stream()
                           .filter(pos -> isBlockingTrap(pos, players))
                           .collect(Collectors.toSet());

            if (!multiTrap.getValue() && trapBlocks.size() > 1)
            {
                return surrounding;
            }

            for (BlockPos trap : trapBlocks)
            {
                if (trapExtend.getValue())
                {
                    EnumFacing r = getFacingRelativeToPlayer(trap, getPlayer());
                    if (r != null)
                    {
                        surrounding.add(getPlayerPos().offset(r, 2));
                    }
                }

                surrounding.remove(trap);
            }
        }

        return surrounding;
    }

    public BlockPos getPlayerPos()
    {
        return deltaY.getValue() && Math.abs(getPlayer().motionY) > 0.1
            ? new BlockPos(getPlayer())
            : PositionUtil.getPosition(getPlayer());
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

    @Override
    public EntityPlayer getPlayer()
    {
        if (freecam.getValue() == SurroundFreecamMode.Origin
            && FREECAM.isEnabled())
        {
            EntityPlayer target = FREECAM.get().getPlayer();
            if (target != null)
            {
                return target;
            }
        }

        return mc.player;
    }

}
