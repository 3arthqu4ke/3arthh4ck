package me.earth.earthhack.impl.modules.combat.autotrap;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autotrap.modes.TrapTarget;
import me.earth.earthhack.impl.modules.combat.autotrap.util.Trap;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AutoTrap extends ObbyListenerModule<ListenerAutoTrap>
{
    /** EnumFacings for the top helpingPos */
    private static final EnumFacing[] TOP_FACINGS = new EnumFacing[]
    {
            EnumFacing.UP,
            EnumFacing.NORTH,
            EnumFacing.WEST,
            EnumFacing.SOUTH,
            EnumFacing.EAST
    };

    protected final Setting<Float> range           =
            register(new NumberSetting<>("Range", 6.0f, 0.0f, 6.0f));
    protected final Setting<Boolean> noScaffold    =
            register(new BooleanSetting("NoScaffold", false));
protected final Setting<Boolean> top               =
            register(new BooleanSetting("Top", true));
    protected final Setting<Boolean> noStep        =
            register(new BooleanSetting("NoStep", false));
    protected final Setting<Boolean> upperBody     =
            register(new BooleanSetting("UpperBody", true));
    protected final Setting<Boolean> legs          =
            register(new BooleanSetting("Legs", false));
    protected final Setting<Boolean> platform      =
            register(new BooleanSetting("Platform", false));
    protected final Setting<Boolean> noDrop        =
            register(new BooleanSetting("NoDrop", false));
    protected final Setting<Integer> extend        =
            register(new NumberSetting<>("Extend", 2, 1, 3));
    protected final Setting<TrapTarget> targetMode =
            register(new EnumSetting<>("Target", TrapTarget.Closest));
    protected final Setting<Float> speed           =
            register(new NumberSetting<>("Speed", 19.0f, 0.0f, 50.0f));
    protected final Setting<Boolean> freeCam       =
            register(new BooleanSetting("FreeCam", false));
    protected final Setting<Boolean> bigExtend =
            register(new BooleanSetting("BigExtend", false));
    protected final Setting<Boolean> helping       =
            register(new BooleanSetting("Helping", false));
    protected final Setting<Boolean> smartTop       =
            register(new BooleanSetting("SmartTop", true));
    protected final Setting<Boolean> noScaffoldPlus       =
            register(new BooleanSetting("NoScaffold+", false));
    protected final Setting<Boolean> upperFace     =
            register(new BooleanSetting("Upper-FP", false));
    protected final Setting<Boolean> instant     =
            register(new BooleanSetting("Instant", false));
    // maybe help bombing?

    /** Players in range mapped to their speed */
    protected final Map<EntityPlayer, Double> speeds = new HashMap<>();
    /** Caches trapping positions for players while looking for a target */
    protected final Map<EntityPlayer, List<BlockPos>> cached = new HashMap<>();
    public final Map<BlockPos, Long> blackList = new ConcurrentHashMap<>();
    /** The current target */
    protected EntityPlayer target;

    public AutoTrap()
    {
        super("AutoTrap", Category.Combat);
        this.setData(new AutoTrapData(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        Managers.ROTATION.setBlocking(false);
    }

    @Override
    protected boolean checkNull()
    {
        boolean checkNull = super.checkNull();
        cached.clear();
        speeds.clear();
        blackList.clear();
        if (checkNull)
        {
            updateSpeed();
        }

        return checkNull;
    }

    @Override
    protected ListenerAutoTrap createListener()
    {
        return new ListenerAutoTrap(this);
    }

    @Override
    public String getDisplayInfo()
    {
        return target == null ? null : target.getName();
    }

    @Override
    protected boolean shouldHelp(EnumFacing facing, BlockPos pos)
    {
        return super.shouldHelp(facing, pos) // ??????
                && helping.getValue()
                && !legs.getValue();
    }

    @Override
    public boolean placeBlock(BlockPos pos)
    {
        if (blackList.containsKey(pos))
        {
            return false;
        }

        return super.placeBlock(pos);
    }

    public EntityPlayer getTarget()
    {
        return target;
    }

    protected TargetResult getTargets(TargetResult result)
    {
        cached.clear();
        updateSpeed();
        EntityPlayer newTarget = calcTarget();
        if (newTarget == null || !newTarget.equals(target))
        {
            listener.placed.clear();
        }

        target = newTarget == null ? target : newTarget;
        if (newTarget == null)
        {
            return result.setValid(false);
        }

        List<BlockPos> newTrapping = cached.get(newTarget);
        if (newTrapping == null)
        {
            newTrapping = getPositions(newTarget);
        }

        return result.setTargets(newTrapping);
    }

    /**
     * Finds the closest valid player,
     * or if mode == Untrapped, the closest
     * untrapped player.
     *
     * @return the target.
     */
    private EntityPlayer calcTarget()
    {
        EntityPlayer closest = null;
        double distance = Double.MAX_VALUE;
        for (EntityPlayer player : mc.world.playerEntities)
        {
            double playerDist = mc.player.getDistanceSq(player);
            if (playerDist < distance && isValid(player))
            {
                closest = player;
                distance = playerDist;
            }
        }

        return closest;
    }

    /**
     * Checks if a given player is valid,
     * that means != null, not dead, not mc.player,
     * not friended, in range, moving slower than
     * the speed setting, and if mode == Untrapped,
     * if hes not trapped.
     *
     * @param player the player to check.
     * @return if the given player can be trapped.
     */
    private boolean isValid(EntityPlayer player)
    {
        if (player != null
                && !EntityUtil.isDead(player)
                && !player.equals(mc.player)
                && !Managers.FRIENDS.contains(player))
        {
            if (player.getDistanceSq(mc.player) <= 36
                    && getSpeed(player) <= speed.getValue())
            {
                if (targetMode.getValue() == TrapTarget.Untrapped)
                {
                    List<BlockPos> positions = getPositions(player);
                    cached.put(player, positions);
                    return positions.stream()
                            .anyMatch(pos ->
                                    mc.world.getBlockState(pos)
                                            .getMaterial()
                                            .isReplaceable());
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Updates the speed map for all
     * players in range.
     */
    private void updateSpeed()
    {
        for (EntityPlayer player : mc.world.playerEntities)
        {
            if (EntityUtil.isValid(player, range.getValue() * 2))
            {
                double xDist = player.posX - player.prevPosX;
                double yDist = player.posY - player.prevPosY;
                double zDist = player.posZ - player.prevPosZ;
                double speed = xDist * xDist + yDist * yDist + zDist * zDist;

                speeds.put(player, speed);
            }
        }
    }

    /**
     * Looks up a player from the speed
     * map and returns his speed or 0.0 if
     * he hasn't been mapped yet.
     *
     * @param player the player whose speed to check.
     * @return the players speed.
     */
    private double getSpeed(EntityPlayer player)
    {
        Double playerSpeed = speeds.get(player);
        if (playerSpeed != null && speed.getValue() != 50.0f)
        {
            return Math.sqrt(playerSpeed) * 20 * 3.6;
        }

        return 0.0;
    }

    /**
     * Gets the positions to trap
     * for the given player. Takes extend
     * and the noScaffold... etc. settings into
     * account.
     *
     * @param player the player to trap.
     * @return trapping positions.
     */
    private List<BlockPos> getPositions(EntityPlayer player)
    {
        List<BlockPos> blocked   = new ArrayList<>();
        BlockPos playerPos = new BlockPos(player);
        if (HoleUtil.isHole(playerPos, false)[0] || extend.getValue() == 1)
        {
            blocked.add(playerPos.up());
        }
        else
        {
            List<BlockPos> unfiltered =
                    new ArrayList<>(PositionUtil.getBlockedPositions(player))
                            .stream()
                            .sorted(Comparator
                                    .comparingDouble(BlockUtil::getDistanceSq))
                            .collect(Collectors.toList());

            List<BlockPos> filtered = new ArrayList<>(unfiltered)
                    .stream()
                    .filter(pos -> mc.world.getBlockState(pos)
                                           .getMaterial()
                                           .isReplaceable()
                                && mc.world.getBlockState(pos.up())
                                           .getMaterial()
                                           .isReplaceable())
                    .collect(Collectors.toList());

            if (extend.getValue() == 3
                    && filtered.size() == 2
                    && unfiltered.size() == 4)
            {
                /*
                    Prevents that a pos like this
                   (x == block, o == air, i = player):

                    o x                              x
                    x i     can extend to this:    x o x
                                                     x i
                */
                if (unfiltered.get(0).equals(filtered.get(0))
                        && unfiltered.get(3).equals(filtered.get(1)))
                {
                    filtered.clear();
                }
            }

            if (extend.getValue() == 2 && filtered.size() > 2
                    || extend.getValue() == 3 && filtered.size() == 3)
            {
                /*
                    Prevents that a pos like this
                   (x == block, o == air, i = player):

                    x  o                               x
                     i      can extend to this:    x   o  x
                                                 x   i    x
                                                   x   x

                   we should, unless he phased in, be able to place on o
                */
                while (filtered.size() > 2)
                {
                    filtered.remove(filtered.size() - 1);
                }
            }

            for (BlockPos pos : filtered)
            {
                blocked.add(pos.up());
            }
        }

        if (blocked.isEmpty())
        {
            blocked.add(playerPos.up());
        }

        List<BlockPos> positions = positionsFromBlocked(blocked);
        // sort so we start placing behind (furthest away) first.
        positions.sort(Comparator.comparingDouble(pos ->
                -BlockUtil.getDistanceSq(pos)));
        // sort by y so we start placing from bottom up.
        positions.sort(Comparator.comparingInt(Vec3i::getY));

        return positions.stream().filter(pos ->
                    BlockUtil.getDistanceSq(pos)
                        <= MathUtil.square(range.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Creates trapping positions
     * from a list of positions that
     * are blocked by a player.
     *
     * @param blockedIn the positions to trap.
     * @return trapping positions.
     */
    private List<BlockPos> positionsFromBlocked(List<BlockPos> blockedIn)
    {
        List<BlockPos> positions = new ArrayList<>();
        if (!noStep.getValue() && !blockedIn.isEmpty())
        {
            BlockPos[] helping = findTopHelping(blockedIn, true);
            for (int i = 0; i < helping.length; i++)
            {
                BlockPos pos = helping[i];
                if (pos != null)
                {
                    if (i == 1
                            && !upperBody.getValue()
                            && (!blockedIn.contains(PositionUtil
                            .getPosition()
                            .up())
                            || !upperFace.getValue())
                            && helping[5] != null)
                    {
                        positions.add(helping[5]);
                    }

                    positions.add(helping[i]);
                    break;
                }
            }
        }

        boolean scaffold = noScaffold.getValue();
        if (top.getValue())
        {
            blockedIn.forEach(pos ->
                positions.addAll(applyOffsets(pos, Trap.TOP, positions)));
        }
        else if (blockedIn.size() == 1
                && smartTop.getValue()
                && scaffold
                && mc.world.getBlockState(blockedIn.get(0)
                                                   .add(Trap.TOP[0]))
                           .getMaterial()
                           .isReplaceable()
                && mc.world.getBlockState(blockedIn.get(0)
                                                   .add(Trap.NO_SCAFFOLD[0]))
                           .getMaterial()
                           .isReplaceable()
                && mc.world.getBlockState(blockedIn.get(0)
                                                   .add(Trap.NO_SCAFFOLD_P[0]))
                           .getMaterial()
                           .isReplaceable())
        {
            blockedIn.forEach(pos ->
                    positions.addAll(applyOffsets(pos, Trap.TOP, positions)));

            if (noScaffoldPlus.getValue())
            {
                blockedIn.forEach(pos ->
                    positions.addAll(
                        applyOffsets(pos, Trap.NO_SCAFFOLD_P, positions)));
            }

            scaffold = false;
            blockedIn.forEach(pos ->
                positions.addAll(
                    applyOffsets(pos, Trap.NO_SCAFFOLD, positions)));
        }

        if (upperBody.getValue()
                || upperFace.getValue()
                    && blockedIn.contains(PositionUtil.getPosition().up()))
        {
            blockedIn.forEach(pos ->
                positions.addAll(applyOffsets(pos, Trap.OFFSETS, positions)));
        }

        // Only apply these if we dont need to extend, otherwise overkill
        if (blockedIn.size() == 1 || bigExtend.getValue())
        {
            if (scaffold)
            {
                blockedIn.forEach(pos ->
                    positions.addAll(
                        applyOffsets(pos, Trap.NO_SCAFFOLD, positions)));
            }

            if (noStep.getValue())
            {
                blockedIn.forEach(pos ->
                    positions.addAll(
                        applyOffsets(pos, Trap.NO_STEP, positions)));
            }

            if (legs.getValue())
            {
                blockedIn.forEach(pos ->
                    positions.addAll(
                        applyOffsets(pos, Trap.LEGS, positions)));
            }

            if (platform.getValue())
            {
                blockedIn.forEach(pos ->
                    positions.addAll(
                        applyOffsets(pos, Trap.PLATFORM, positions)));
            }

            if (noDrop.getValue())
            {
                blockedIn.forEach(pos ->
                    positions.addAll(
                        applyOffsets(pos, Trap.NO_DROP, positions)));
            }
        }

        return positions;
    }

    /**
     * Finds helping blocks for the top pos.
     * Returns an Array with possible helping Positions.
     * The Indices work this way:
     * 0 : has a facing and no entities,
     * 1 : no facing no entities,
     * 2 : has a facing but entities,
     * 3 : no facing, entities blocking
     * 4 : random pos when everything is shit.
     * 5 : A helping Pos for index 1.
     *
     * @param positions positions to find a helpingPos for. (not empty!)
     * @param first used for recursion should always be <tt>true</tt>.
     * @return possible helping positions.
     */
    private BlockPos[] findTopHelping(List<BlockPos> positions, boolean first)
    {
        BlockPos[] bestPos = new BlockPos[] {
                null,
                null,
                null,
                null,
                positions.get(0).up().north(),
                null
        };
        for (BlockPos pos : positions)
        {
            BlockPos up = pos.up();
            //TODO: Sort facings so that we dont block piston aura
            for (EnumFacing facing : TOP_FACINGS)
            {
                BlockPos helping = up.offset(facing);
                // return instantly, no helping needed
                if (!mc.world
                        .getBlockState(helping)
                        .getMaterial()
                        .isReplaceable())
                {
                    bestPos[0] = helping;
                    return bestPos;
                }

                EnumFacing helpingFace = BlockUtil.getFacing(helping, HELPER);
                byte blockingFactor = helpingEntityCheck(helping);
                if (helpingFace == null)
                {
                    switch (blockingFactor)
                    {
                        case 0:
                            if (first && bestPos[5] == null)
                            {
                                List<BlockPos> hPositions = new ArrayList<>();
                                for (BlockPos hPos : positions)
                                {
                                    // won't check up up helping I think
                                    hPositions.add(hPos.down());
                                }

                                bestPos[5] =
                                        findTopHelping(hPositions, false)[0];
                            }
                            else
                            {
                                break;
                            }

                            bestPos[1] = helping;
                            break;
                        case 1:
                            bestPos[3] = helping;
                            break;
                        case 2:
                            break;
                    }
                }
                else
                {
                    switch (blockingFactor)
                    {
                        case 0:
                            bestPos[0] = helping;
                            break;
                        case 1:
                            bestPos[2] = helping;
                            break;
                        case 2:
                            break;
                    }
                }
            }
        }

        return bestPos;
    }

    /**
     * EntityCheck for the helping position.
     *
     * @param pos the position to check.
     * @return 0 if free of entities, 1 if crystal, 2 if blocking.
     */
    private byte helpingEntityCheck(BlockPos pos)
    {
        byte blocking = 0;
        for (Entity entity : mc.world
                .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)))
        {
            if (entity == null
                    || EntityUtil.isDead(entity)
                    || !entity.preventEntitySpawning
                    || (entity instanceof EntityPlayer
                    && !BlockUtil.isBlocking(pos,
                    (EntityPlayer) entity,
                    blockingType.getValue())))
            {
                continue;
            }

            if (entity instanceof EntityEnderCrystal && attack.getValue())
            {
                float damage = DamageUtil.calculate(entity, mc.player);
                if (damage <= EntityUtil.getHealth(mc.player) + 1.0)
                {
                    blocking = 1;
                    continue;
                }
            }

            return 2;
        }

        return blocking;
    }

    /**
     * Applies the given offsets to the position and
     * returns a list that doesnt contain any positions
     * already contained by alreadyAdded.
     *
     * @param pos the pos to apply the offsets to.
     * @param offsets the offsets.
     * @param alreadyAdded list to prevent duplicates.
     * @return offsets for the position.
     */
    private List<BlockPos> applyOffsets(BlockPos pos,
                                        Vec3i[] offsets,
                                        List<BlockPos> alreadyAdded)
    {
        List<BlockPos> positions = new ArrayList<>();
        for (Vec3i vec3i : offsets)
        {
            BlockPos offset = pos.add(vec3i);
            if (!alreadyAdded.contains(offset))
            {
                positions.add(offset);
            }
        }

        return positions;
    }

    protected boolean instantRotationCheck(BlockPos pos)
    {
        return rotate.getValue() != Rotate.Normal || RotationUtil.isLegit(pos);
    }

    protected void runInstantTick(PacketEvent.Receive<?> event)
    {
        event.addPostEvent(() ->
        {
            if (mc.player == null || mc.world == null)
            {
                return;
            }

            MotionUpdateEvent motionUpdateEvent = new MotionUpdateEvent(
                Stage.PRE,
                Managers.POSITION.getX(),
                Managers.POSITION.getY(),
                Managers.POSITION.getZ(),
                Managers.ROTATION.getServerYaw(),
                Managers.ROTATION.getServerPitch(),
                Managers.POSITION.isOnGround());

            this.listener.invoke(motionUpdateEvent);
            motionUpdateEvent = new MotionUpdateEvent(Stage.POST,
                                                      motionUpdateEvent);
            this.listener.invoke(motionUpdateEvent);
        });
    }

}
