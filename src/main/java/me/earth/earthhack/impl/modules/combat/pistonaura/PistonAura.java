package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.pistonaura.modes.PistonTarget;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonData;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonStage;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.stream.Collectors;

//TODO: Rewrite
//TODO: This is v chinese
//TODO: redstone torches should have different offsets
//TODO: levers, buttons and dayLightSensors maybe?
public class PistonAura extends BlockPlacingModule
{
    protected final Setting<Boolean> multiDirectional =
            register(new BooleanSetting("MultiDirectional", false));
    protected final Setting<Boolean> explode =
            register(new BooleanSetting("Break", true));
    protected final Setting<Integer> breakDelay =
            register(new NumberSetting<>("BreakDelay", 50, 0, 500));
    protected final Setting<Float> breakRange =
            register(new NumberSetting<>("BreakRange", 4.5f, 0.1f, 6.0f));
    protected final Setting<Float> breakTrace =
            register(new NumberSetting<>("BreakTrace", 3.0f, 0.1f, 6.0f));
    protected final Setting<Float> placeRange =
            register(new NumberSetting<>("PlaceRange", 4.5f, 0.1f, 6.0f));
    protected final Setting<Float> placeTrace =
            register(new NumberSetting<>("PlaceTrace", 4.5f, 0.1f, 6.0f));
    protected final Setting<Integer> coolDown =
            register(new NumberSetting<>("Cooldown", 500, 0, 500));
    protected final Setting<Boolean> suicide  =
            register(new BooleanSetting("Suicide", false));
    protected final Setting<Boolean> newVer   =
            register(new BooleanSetting("1.13+", false));
    protected final Setting<PistonTarget> targetMode =
            register(new EnumSetting<>("Target", PistonTarget.Calc));
    protected final Setting<Boolean> instant =
            register(new BooleanSetting("Instant", true));
    protected final Setting<Integer> confirmation =
            register(new NumberSetting<>("Confirm", 250, 0, 1000));
    protected final Setting<Integer> next =
            register(new NumberSetting<>("NextPhase", 1000, 0, 5000));
    protected final Setting<Boolean> explosions =
            register(new BooleanSetting("Explosions", true));
    protected final Setting<Boolean> destroyEntities =
            register(new BooleanSetting("DestroyEntities", false));
    protected final Setting<Boolean> multiChange =
            register(new BooleanSetting("MultiChange", false));
    protected final Setting<Boolean> change =
            register(new BooleanSetting("Change", false));

    /** Handles blocks we clicked. */
    protected final Set<Block> clicked = new HashSet<>();
    /** Actions executed after onUpdateWalkingPlayer. */
    protected final Queue<Runnable> actions = new LinkedList<>();
    /** Handles delay for breaking crystals. */
    protected final StopWatch breakTimer = new StopWatch();
    /** Handles delay from sending the packet until next phase. */
    protected final StopWatch packetTimer = new StopWatch();
    /** Handles delay for the next stage. */
    protected final StopWatch nextTimer = new StopWatch();
    /** The current stage that is being executed. */
    protected PistonStage stage = PistonStage.PISTON;
    /** The hotbar slot for pistons. */
    protected int pistonSlot   = -1;
    /** The hotbar slot for redstone. */
    protected int redstoneSlot = -1;
    /** The hotbar slot for crystals. */
    protected int crystalSlot  = -1;
    /** The currently targeted player. */
    protected EntityPlayer target;
    /** Current PistonData. */
    protected PistonData current;
    /** Current index for the order of the PistonStages. */
    protected int index;
    /** Entity of the current crystal being attacked. */
    protected int entityId;
    /** Marks that we should reset and call findTarget again. */
    protected boolean reset;

    public PistonAura()
    {
        super("PistonAura", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerDestroyEntities(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerBlockChange(this));

        super.packet.setValue(false);
        super.delay.setValue(0);

        this.setData(new PistonAuraData(this));

        this.rotate.setValue(Rotate.Normal);
        this.rotate.addObserver(event -> // TODO: WHY do the others not work???
        {
            if (event.getValue() != Rotate.Normal)
            {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public String getDisplayInfo()
    {
        if (EntityUtil.isValid(target, 9.0f))
        {
            return target.getName();
        }

        return null;
    }

    @Override
    protected void onEnable()
    {
        pistonSlot   = -1;
        redstoneSlot = -1;
        crystalSlot  = -1;
        current      = null;
        index        = 0;
        reset        = false;
        packetTimer.reset();
        nextTimer.reset();

        if (mc.player == null)
        {
            this.disable();
            return;
        }

        slot = mc.player.inventory.currentItem;
    }

    /**
     * Invokes {@link Module#disable()}, then sends the given
     * message.
     *
     * @param message the message to broadcast afterwards.
     */
    public void disableWithMessage(String message)
    {
        this.disable();
        Managers.CHAT.sendDeleteMessage(message,
                                        this.getDisplayName(),
                                        ChatIDs.MODULE);
    }

    /**
     * Note that this method should only be called,
     * if the current data is null or invalid. Because
     * this method sets {@link PistonAura#index} to 0 and
     * {@link PistonAura#stage} to null.
     *
     * @return PistonData for a new target or null.
     */
    protected PistonData findTarget()
    {
        index = 0;
        stage = null;
        reset = false;
        packetTimer.reset();
        nextTimer.reset();
        List<PistonData> data = new ArrayList<>();

        switch (targetMode.getValue())
        {
            case FOV:
                EntityPlayer closest = null;
                double closestAngle  = 360.0;
                BlockPos pos         = null;
                for (EntityPlayer player : mc.world.playerEntities)
                {
                    if (!EntityUtil.isValid(player, 9.0f))
                    {
                        continue;
                    }

                    BlockPos playerPos = PositionUtil.getPosition(player);

                    if (!suicide.getValue()
                            && PositionUtil.getPosition().equals(playerPos))
                    {
                        continue;
                    }

                    if (!HoleUtil.isHole(playerPos, false)[0]
                        && !HoleUtil.is2x1(playerPos))
                    {
                        continue;
                    }

                    double angle = RotationUtil.getAngle(player, 1.4);
                    if (angle < closestAngle)
                    {
                        closest      = player;
                        closestAngle = angle;
                        pos          = playerPos;
                    }
                }

                if (closest != null)
                {
                    data.addAll(checkPlayer(closest, pos));
                }
                break;
            case Closest:
                EntityPlayer closestEnemy = EntityUtil.getClosestEnemy();
                if (closestEnemy != null)
                {
                    BlockPos playerPos = PositionUtil.getPosition(closestEnemy);

                    if (!suicide.getValue()
                            && PositionUtil.getPosition().equals(playerPos))
                    {
                        break;
                    }

                    if (HoleUtil.isHole(playerPos, false)[0]
                            || HoleUtil.is2x1(playerPos))
                    {
                        data.addAll(checkPlayer(closestEnemy, playerPos));
                    }
                }
                break;
            case Calc:
                for (EntityPlayer player : mc.world.playerEntities)
                {
                    if (!EntityUtil.isValid(player, 9.0f))
                    {
                        continue;
                    }

                    BlockPos playerPos = PositionUtil.getPosition(player);

                    if (!suicide.getValue()
                            && PositionUtil.getPosition().equals(playerPos))
                    {
                        continue;
                    }

                    if (HoleUtil.isHole(playerPos, false)[0]
                            || HoleUtil.is2x1(playerPos))
                    {
                        data.addAll(checkPlayer(player, playerPos));
                    }
                }
                break;
            default:
        }

        if (data.isEmpty())
        {
            return null;
        }

        List<PistonData> nonMulti = data.stream()
                                        .filter(d ->
                                            !d.isMulti())
                                        .collect(Collectors.toList());
        if (!nonMulti.isEmpty())
        {
            // TODO: make PistonData implement Comparable
            nonMulti.sort(Comparator.comparingDouble(d ->
                                 mc.player.getDistanceSq(d.getCrystalPos())));
            return nonMulti.get(0);
        }

        data.sort(Comparator.comparingDouble(d ->
                                mc.player.getDistanceSq(d.getCrystalPos())));
        return data.get(0);
    }

    /**
     * Checks all Positions around the given player
     * for valid PistonData. If no valid position has
     * been found and the block above the player is clear
     * positions above will also be checked.
     *
     * @param player the player get PistonData for.
     * @param pos the position of the player.
     * @return A list of PistonData, all valid.
     */
    private List<PistonData> checkPlayer(EntityPlayer player, BlockPos pos)
    {
        List<PistonData> data = new ArrayList<>(checkFacings(player, pos));

        // Similar to mining the top block of the AutoTrap,
        // Crystal will hover over the enemy and explode downwards.
        if (data.isEmpty()
                && mc.world
                    .getBlockState(pos.up(2))
                    .getMaterial()
                    .isReplaceable())
        {
            data.addAll(checkFacings(player, pos.up()));
        }

        return data;
    }

    /**
     * Called when a blockChange packet is received for when
     * the piston/redstone on our piston/redstone position is removed.
     * {@link ListenerBlockChange}, {@link ListenerMultiBlockChange}
     *
     * @param pos the position to update.
     * @param state the state that is updated to.
     * @param dataPos pos needs to equal dataPos.
     * @param block1 a blockType for the state before.
     * @param block2 a blockType for the state before.
     * @return <tt>true</tt> if the module should reset after this.
     */
    protected boolean checkUpdate(BlockPos pos,
                                  IBlockState state,
                                  BlockPos dataPos,
                                  Block block1,
                                  Block block2)
    {
        if (pos.equals(dataPos))
        {
            IBlockState before = mc.world.getBlockState(pos);
            return (before.getBlock() == block1 || before.getBlock() == block2)
                    && state.getMaterial().isReplaceable();
        }

        return false;
    }

    /**
     * Evaluates PistonData for all
     * {@link EnumFacing#HORIZONTALS} around
     * the given position.
     *
     * @param player the player on that position.
     * @param pos the position to get PistonData for.
     * @return A list of PistonData that is valid.
     */
    private List<PistonData> checkFacings(EntityPlayer player, BlockPos pos)
    {
        List<PistonData> data = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos offset = pos.offset(facing);
            if (BlockUtil.canPlaceCrystal(offset, true, newVer.getValue()))
            {
                PistonData d = evaluate(new PistonData(player, offset, facing));
                if (d.isValid())
                {
                    data.add(d);
                }
            }
        }

        return data;
    }

    /**
     * Evaluates the given PistonData.
     * Will add the RedStone, PistonPosition
     * and execution order with PistonStages
     * to the data. Only if all checks have been
     * passed and the Positions are valid,
     * the data will return with
     * {@link PistonData#isValid()} == <tt>true</tt>.
     *
     * @param data the PistonData to evaluate.
     * @return the given data, evaluated.
     */
    private PistonData evaluate(PistonData data)
    {
        BlockPos crystal = data.getCrystalPos();
        double placeDist = mc.player.getDistanceSq(crystal);
        // Check if crystalPos lies within place and placetrace.
        if (placeDist > MathUtil.square(placeRange.getValue())
                || placeDist > MathUtil.square(placeTrace.getValue())
                    && !RayTraceUtil.raytracePlaceCheck(mc.player, crystal))
        {
            return data;
        }

        double breakDist = mc.player.getDistanceSq(crystal.getX() + 0.5,
                                                   crystal.getY() + 1.0,
                                                   crystal.getZ() + 0.5);

        // Check if the crystal will lie within break and breaktrace.
        if (breakDist > MathUtil.square(breakRange.getValue())
                || breakDist > MathUtil.square(breakTrace.getValue())
                    && !RayTraceUtil.canBeSeen(
                            new Vec3d(crystal.getX() + 0.5,
                                      crystal.getY() + 2.7,
                                      crystal.getZ() + 0.5),
                            mc.player))
        {
            return data;
        }

        // Order in which the PistonStages will be executed.
        PistonStage[] order = new PistonStage[4];
        // Possible positions for the piston.
        BlockPos piston  = data.getCrystalPos().offset(data.getFacing()).up();
        // Position one further away, can still push the crystal into the target
        BlockPos piston1 = piston.offset(data.getFacing());
        // If we are using the piston1 position
        boolean using1 = false;
        // PistonPos Blockstate.
        IBlockState toPush = mc.world.getBlockState(piston);
        // piston1 pos Blockstate
        IBlockState piston1State = mc.world.getBlockState(piston1);
        // Where we can place the piston on.
        EnumFacing placeFacing  = BlockUtil.getFacing(piston);
        // If theres a crystal on the pos and we dont have to use a crystal.
        boolean noCrystal = false;

        for (Entity entity :
                mc.world.getEntitiesWithinAABB(Entity.class,
                            new AxisAlignedBB(crystal, crystal.add(1, 2, 1))))
        {
            if (entity == null || EntityUtil.isDead(entity))
            {
                continue;
            }

            if (entity instanceof EntityEnderCrystal
                    && crystal.equals(entity.getPosition().down()))
            {
                noCrystal = true;
                using1 = true;
                continue;
            }

            return data;
        }

        // If too far away, use piston1.
        if (mc.player.getDistanceSq(piston) >
                                        MathUtil.square(placeRange.getValue()))
        {
            using1 = true;
            if (mc.player.getDistanceSq(piston1) >
                                        MathUtil.square(placeRange.getValue()))
            {
                return data;
            }
        }

        // Check if theres a piston already on one of the positions.
        boolean noPiston = false;

        // Check if a piston can be placed on the first pos.
        if (!toPush.getMaterial().isReplaceable())
        {
            // check if that position already is a piston
            if (toPush.getBlock() == Blocks.PISTON
                    || toPush.getBlock() == Blocks.STICKY_PISTON)
            {
                // if the facing is good we dont need to use a piston at all.
                if (toPush.getProperties().get(BlockDirectional.FACING) ==
                                                data.getFacing().getOpposite())
                {
                    noPiston = true;
                    using1 = false;
                }
                else
                {
                    // bad piston, we have to use the other piston pos.
                    using1 = true;
                }
            }
            else
            {
                // if we cant push the block on the piston return
                if (!mc.world.getBlockState(piston1)
                             .getMaterial()
                             .isReplaceable()
                        && !(piston1State.getBlock() == Blocks.PISTON
                            || piston1State.getBlock() == Blocks.STICKY_PISTON)
                        || (toPush.getPushReaction() != EnumPushReaction.DESTROY
                            && !BlockPistonBase.canPush(toPush,
                                                    mc.world,
                                                    piston,
                                                    data.getFacing()
                                                            .getOpposite(),
                                                    false,
                                                    data.getFacing()
                                                            .getOpposite())))
                {
                    return data;
                }

                // we can push the block on the position
                using1 = true;
            }
        }

        // In case piston1 is a piston with bad facing we cant use it.
        boolean cantPiston1 = false;
        // Check if piston1 is a piston
        if (piston1State.getBlock() == Blocks.PISTON
                || piston1State.getBlock() == Blocks.STICKY_PISTON)
        {
            if (piston1State.getProperties().get(BlockDirectional.FACING) ==
                                                data.getFacing().getOpposite()
                    && !((Boolean) piston1State.getProperties()
                                                .get(BlockPistonBase.EXTENDED)))
            {
                using1   = true;
                noPiston = true;
            }
            else
            {
                cantPiston1 = true;
            }
        }

        if (noPiston)
        {
            for (EnumFacing facing :
                                getRedstoneFacings(data.getFacing(), using1))
            {
                BlockPos redstone = using1
                        ? piston1.offset(facing)
                        : piston.offset(facing);

                if (mc.player.getDistanceSq(redstone) >
                        MathUtil.square(placeRange.getValue())
                        || !mc.world
                                .getBlockState(redstone)
                                .getMaterial()
                                .isReplaceable()
                        || checkEntities(redstone))
                {
                    continue;
                }

                data.setRedstonePos(redstone);
                break;
            }

            if (data.getRedstonePos() != null)
            {
                order[0] = noCrystal ? null : PistonStage.CRYSTAL;
                order[1] = PistonStage.REDSTONE;
                order[2] = null;
                order[3] = PistonStage.BREAK;

                data.setOrder(order);
                data.setValid(true);
                return data;
            }
            else if (!using1) // we can still try with piston1
            {
                using1 = true;
            }
            else
            {
                return data;
            }
        }

        // Check if we dont need to use redstone. if the redstone is around
        // the first position we have to use the piston1 pos, or else its gonna
        // extend onto the crystal pos.
        boolean noR/*edstone*/  = false;

        // We could use mc.world.isBlockPowered, but I dont trust that one
        for (EnumFacing facing : EnumFacing.values())
        {
            if (facing != data.getFacing().getOpposite())
            {
                IBlockState state  =
                        mc.world.getBlockState(piston.offset(facing));
                IBlockState state1 =
                        mc.world.getBlockState(piston1.offset(facing));

                if (state.getBlock() == Blocks.REDSTONE_TORCH
                        || state.getBlock() == Blocks.REDSTONE_BLOCK)
                {
                    using1 = true;
                }

                if (state1.getBlock() == Blocks.REDSTONE_BLOCK
                        || state1.getBlock() == Blocks.REDSTONE_TORCH)
                {
                    noR = true;
                    using1 = true;
                    break;
                }
            }
        }

        // Check if entities block the first piston pos.
        for (Entity entity : mc.world
                .getEntitiesWithinAABB(Entity.class,
                                       new AxisAlignedBB(piston)))
        {
            if (entity == null
                    || EntityUtil.isDead(entity)
                    || !entity.preventEntitySpawning)
            {
                continue;
            }

            using1 = true;
            break;
        }

        // check the facing the piston on the first pos will assume
        EnumFacing pistonFacing = getFacing(piston, null);
        if (!using1
                && (pistonFacing == EnumFacing.UP
                    || pistonFacing == EnumFacing.DOWN
                    || !multiDirectional.getValue()
                        && pistonFacing != data.getFacing().getOpposite()))
        {
            using1 = true; //TODO: use cantPiston1 here too, make a cantPiston
        }

        if (using1)
        {
            // check the facing the piston on the second pos will assume
            EnumFacing pistonFacing1 = getFacing(piston1, null);
            if (pistonFacing1 == EnumFacing.UP
                    || pistonFacing1 == EnumFacing.DOWN
                    || !multiDirectional.getValue()
                        && pistonFacing1 != data.getFacing().getOpposite())
            {
                return data;
            }

            placeFacing = BlockUtil.getFacing(piston1);
            if (pistonFacing1 != data.getFacing().getOpposite())
            {
                data.setMulti(true);
            }
        }

        // check entities on piston1 and placeRange
        if (using1
                && (checkEntities(piston1)
                    || mc.player.getDistanceSq(piston1) >
                            MathUtil.square(placeRange.getValue())))
        {
            return data;
        }

        EnumFacing redstoneFacing = null;
        // find redstone position
        if (!noR)
        {
            for (EnumFacing facing :
                                getRedstoneFacings(data.getFacing(), using1))
            {
                BlockPos redstone = using1
                        ? piston1.offset(facing)
                        : piston.offset(facing);

                if (mc.player.getDistanceSq(redstone) >
                        MathUtil.square(placeRange.getValue())
                        || !mc.world
                              .getBlockState(redstone)
                              .getMaterial()
                              .isReplaceable()
                        || checkEntities(redstone))
                {
                    continue;
                }

                redstoneFacing = BlockUtil.getFacing(redstone);
                if (redstoneFacing != null || placeFacing != null && using1)
                {
                    data.setRedstonePos(redstone);
                    break;
                }
            }
        }

        if (!noR && data.getRedstonePos() == null
                || using1 && !mc.world.getBlockState(piston1)
                                      .getMaterial()
                                      .isReplaceable()
                || using1 && cantPiston1)
        {
            return data;
        }

        if (!using1 && pistonFacing != data.getFacing().getOpposite())
        {
            data.setMulti(true);
        }

        data.setPistonPos(using1 ? piston1 : piston);
        boolean s = redstoneFacing != null && placeFacing == null && using1;

        if (noR)
        {
            order[0] = null;
            order[1] = noCrystal ? null : PistonStage.CRYSTAL;
            order[2] = PistonStage.PISTON;
        }
        else
        {
            order[0] = s ? PistonStage.REDSTONE : PistonStage.PISTON;
            order[1] = noCrystal ? null : PistonStage.CRYSTAL;
            order[2] = s ? PistonStage.PISTON : PistonStage.REDSTONE;
        }

        order[3] = PistonStage.BREAK;
        data.setOrder(order);
        data.setValid(true);

        return data;
    }

    /**
     * Similar to {@link EnumFacing
     * #getDirectionFromEntityLiving(BlockPos, EntityLivingBase)}.
     *
     * @param pos the pos to check.
     * @return the direction the block will face after placing it.
     */
    protected EnumFacing getFacing(BlockPos pos, float[] rotations)
    {
        if (Math.abs(mc.player.posX - (double) ((float) pos.getX() + 0.5f))
                < 2.0
            && Math.abs(mc.player.posZ - (double) ((float) pos.getZ() + 0.5f))
                < 2.0)
        {
            double y = mc.player.posY + mc.player.getEyeHeight();

            if (y - (double) pos.getY() > 2.0)
            {
                return EnumFacing.UP;
            }

            if ((double) pos.getY() - y > 0.0)
            {
                return EnumFacing.DOWN;
            }
        }

        if (rotations == null)
        {
            EnumFacing facing = BlockUtil.getFacing(pos);
            rotations = RotationUtil.getRotations(
                    facing == null
                            ? pos
                            : pos.offset(facing),
                    facing == null
                            ? null
                            : facing.getOpposite());
        }

        return EnumFacing.byHorizontalIndex(MathHelper.floor(
                    (double) (rotations[0] * 4.0F / 360.0F) + 0.5D) & 3)
                .getOpposite();
    }

    /**
     * Checks the given position for blocking entities.
     *
     * @param pos the position to check.
     * @return <tt>true</tt> if Entities block the position.
     */
    protected boolean checkEntities(BlockPos pos)
    {
        for (Entity entity : mc.world
                .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)))
        {
            if (entity == null
                    || EntityUtil.isDead(entity)
                    || !entity.preventEntitySpawning)
            {
                continue;
            }

            return true;
        }

        return false;
    }

    /**
     * Returns facings that the redstone block can assume
     * around the piston. The argument piston1 should be
     * <tt>true</tt> if theres 1 block of space between
     * crystal and piston, that way the hitBox of the
     * crystal blocks less facings.
     *
     * @param facing facing from crystalPos to pistonPos.
     * @param piston1 if less positions are blocked.
     * @return possible redstone facings.
     */
    private EnumFacing[] getRedstoneFacings(EnumFacing facing, boolean piston1)
    {
        if (piston1)
        {
            EnumFacing[] result = new EnumFacing[5];
            int i = 0;
            for (EnumFacing f : EnumFacing.values())
            {
                if (f != facing.getOpposite())
                {
                    result[i] = f;
                    i++;
                }
            }

            return result;
        }

        return new EnumFacing[]{EnumFacing.DOWN, facing};
    }

    /**
     * @return the inventory slot for the item belonging
     *         to the current {@link PistonAura#stage}.
     */
    protected int getSlot()
    {
        switch (stage)
        {
            case CRYSTAL:
                return crystalSlot;
            case PISTON:
                return pistonSlot;
            case REDSTONE:
                return redstoneSlot;
            case BREAK:
                break;
            default:
        }

        return -1;
    }

    /**
     * @return <tt>true</tt>, if the item in our
     *          redstoneSlot is a redstone torch.
     */
    public boolean usingTorches()
    {
        if (redstoneSlot != -1)
        {
            ItemStack stack;
            if (redstoneSlot == -2)
            {
                stack = mc.player.getHeldItemOffhand();
            }
            else
            {
                stack = mc.player.inventory.getStackInSlot(redstoneSlot);
            }

            if (stack.getItem() instanceof ItemBlock)
            {
                return ((ItemBlock) stack.getItem()).getBlock() ==
                                                        Blocks.REDSTONE_TORCH;
            }
        }

        return false;
    }

}
