package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.attack.AttackingModule;
import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyData;
import me.earth.earthhack.impl.util.helpers.blocks.modes.FastHelping;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.ncp.Visible;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A {@link BlockPlacingModule} with more functionality.
 */
public abstract class ObbyModule extends BlockPlacingModule
        implements AttackingModule
{
    /** An Instance of {@link BlockStateHelper}. */
    public static final BlockStateHelper HELPER = new BlockStateHelper();

    static
    {
        Bus.EVENT_BUS.register(new EventListener<MotionUpdateEvent>
                (MotionUpdateEvent.class, Integer.MAX_VALUE)
        {
            @Override
            public void invoke(MotionUpdateEvent event)
            {
                if (event.getStage() == Stage.POST)
                {
                    HELPER.clearAllStates();
                }
            }
        });
    }

    public final Setting<Boolean> attack =
            register(new BooleanSetting("Attack", false))
                .setComplexity(Complexity.Medium);
    public final Setting<Pop> pop =
            register(new EnumSetting<>("Pop", Pop.None))
                .setComplexity(Complexity.Expert);
    public final Setting<Integer> popTime =
            register(new NumberSetting<>("Pop-Time", 500, 0, 500))
                .setComplexity(Complexity.Expert);
    public final Setting<Integer> cooldown =
            register(new NumberSetting<>("Cooldown", 500, 0, 500))
                .setComplexity(Complexity.Medium);
    public final Setting<Boolean> antiWeakness =
            register(new BooleanSetting("AntiWeakness", false))
                .setComplexity(Complexity.Medium);
    public final Setting<Integer> breakDelay =
            register(new NumberSetting<>("BreakDelay", 250, 0, 500))
                .setComplexity(Complexity.Medium);
    public final Setting<Boolean> rayTraceBypass =
            register(new BooleanSetting("RayTrace-Bypass", false))
                .setComplexity(Complexity.Expert);
    // TODO: force RayTraceBypass
    public final Setting<FastHelping> fastHelpingBlocks =
            register(new EnumSetting<>("Fast-Helping", FastHelping.Fast))
                .setComplexity(Complexity.Expert);

    public final Setting<Boolean> attackAny =
            register(new BooleanSetting("Attack-Any", false))
                .setComplexity(Complexity.Expert);
    public final Setting<Double> attackRange =
            register(new NumberSetting<>("Attack-Range", 6.0, 0.0, 6.0))
                .setComplexity(Complexity.Medium);
    public final Setting<Double> attackTrace =
            register(new NumberSetting<>("Attack-Trace", 3.0, 0.0, 6.0))
                .setComplexity(Complexity.Expert);

    /** Timer to manage attackDelay */
    public final StopWatch attackTimer = new StopWatch();
    /** The attack packet for a blocking crystal */
    public CPacketUseEntity attacking = null;

    protected ObbyModule(String name, Category category)
    {
        super(name, category);
        this.setData(new ObbyData<>(this));
    }

    @Override
    public Pop getPop()
    {
        return pop.getValue();
    }

    @Override
    public double getRange()
    {
        return attackRange.getValue();
    }

    @Override
    public int getPopTime()
    {
        return popTime.getValue();
    }

    @Override
    public double getTrace()
    {
        return attackTrace.getValue();
    }

    @Override
    public String getDisplayInfo()
    {
        double time = MathUtil.round(timer.getTime() / 1000.0, 1);
        return (time > 0.5
                    ? time > 1.0
                        ? time > 1.5
                            ? TextColor.GREEN
                            : TextColor.YELLOW
                        : TextColor.GOLD
                    : TextColor.RED)
                + time;
    }

    @Override
    protected void onEnable()
    {
        attacking = null;
    }

    @Override
    public boolean execute()
    {
        lastSlot = -1;
        if (!packets.isEmpty())
        {
            if (attacking != null)
            {
                CooldownBypass cdb = cooldownBypass.getValue();
                boolean switched = false;
                int slot = -1;
                if (!DamageUtil.canBreakWeakness(true))
                {
                    if (cooldown.getValue() != 0
                        || !antiWeakness.getValue()
                        || (slot = DamageUtil.findAntiWeakness()) == -1)
                    {
                        filterPackets();
                        if (packets.isEmpty())
                        {
                            return false;
                        }

                        return super.execute();
                    }

                    lastSlot = mc.player.inventory.currentItem;

                    if (cdb == CooldownBypass.None) {
                        InventoryUtil.switchTo(slot);
                    } else {
                        cdb.switchTo(slot);
                        switched = true;
                    }
                }

                mc.player.connection.sendPacket(attacking);
                Swing.Packet.swing(EnumHand.MAIN_HAND);

                // IK ITS ALWAYS TRUE BUT STILL
                //noinspection ConstantConditions
                if (switched && cdb != CooldownBypass.None)
                {
                    cdb.switchTo(slot);
                }

                attackTimer.reset();
                attacking = null;
            }

            return super.execute();
        }

        return false;
    }

    protected void filterPackets()
    {
        // Kinda eh but whatever
        boolean awaitingSwing = false;
        CPacketPlayer.Rotation rotation = null;
        List<Packet<?>> toRemove = new ArrayList<>();
        for (Packet<?> p : packets)
        {
            if (p instanceof CPacketPlayerTryUseItemOnBlock)
            {
                CPacketPlayerTryUseItemOnBlock c =
                        (CPacketPlayerTryUseItemOnBlock) p;

                BlockPos pos =
                        c.getPos().offset(c.getDirection());

                for (Entity entity :
                        mc.world.getEntitiesWithinAABB(
                                Entity.class,
                                new AxisAlignedBB(pos)))
                {
                    if (!EntityUtil.isDead(entity)
                            && entity.preventEntitySpawning
                            && (RotationUtil.getRotationPlayer()
                            .equals(mc.player)
                            || !mc.player.equals(entity)))
                    {
                        if (rotation != null)
                        {
                            toRemove.add(rotation);
                        }

                        toRemove.add(p);
                        awaitingSwing = true;
                    }
                }
            }
            else if (p instanceof CPacketPlayer.Rotation)
            {
                rotation = (CPacketPlayer.Rotation) p;
            }
            else if (awaitingSwing
                    && p instanceof CPacketAnimation)
            {
                awaitingSwing = false;
                toRemove.add(p);
            }
        }

        packets.removeAll(toRemove);
    }

    @Override
    protected boolean sneak(Collection<Packet<?>> packets)
    {
        return smartSneak.getValue()
                && !(Managers.ACTION.isSneaking()
                    || packets.stream()
                              .anyMatch(p ->
                                SpecialBlocks.ACCESS_CHECK.test(p, HELPER)));
    }

    @Override
    public boolean entityCheck(BlockPos pos)
    {
        CPacketUseEntity attackPacket = null;
        boolean crystals = false;
        float currentDmg = Float.MAX_VALUE;
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

            if (entity instanceof EntityEnderCrystal
                    && attackTimer.passed(breakDelay.getValue())
                    && attack.getValue()
                    && Managers.SWITCH.getLastSwitch() >= cooldown.getValue())
            {
                float damage = DamageUtil.calculate(entity,
                                                    getPlayerForRotations());
                if (damage < currentDmg)
                {
                    currentDmg = damage;
                    if (pop.getValue().shouldPop(damage, popTime.getValue()))
                    {
                        attackPacket = new CPacketUseEntity(entity);
                        continue;
                    }
                }

                crystals = true;
                continue;
            }

            if (blockingType.getValue() != BlockingType.Crystals
                    || !(entity instanceof EntityEnderCrystal))
            {
                return false;
            }
        }

        if (crystals
                && attackPacket == null
                && blockingType.getValue() != BlockingType.Crystals)
        {
            return false;
        }

        if (attackPacket != null)
        {
            attacking = attackPacket;
        }

        return true;
    }

    /**
     * Places a Block at the given position.
     *
     * @param pos the position to place a block at.
     * @return <tt>true</tt> if successful.
     */
    public boolean placeBlock(BlockPos pos)
    {
        if (smartRay.getValue() != RayTraceMode.Fast
            && (!rayTraceBypass.getValue() || Visible.INSTANCE.check(pos)))
        {
            Entity entity = getPlayerForRotations();
            Ray forceRay = null;
            Ray forceHelpingRay = null;
            Ray dumbRay = null;
            Ray dumbHelpingRay = null;
            Ray ray = RayTraceFactory.fullTrace(entity, HELPER, pos, -1.0);
            if (ray == null
                || shouldHelp(ray.getFacing(), pos)
                || !ray.getPos().offset(ray.getFacing()).equals(pos)
                || !ray.isLegit()
                    && (smartRay.getValue() == RayTraceMode.Smart
                        || smartRay.getValue() == RayTraceMode.Force))
            {
                if (ray != null
                        && ray.getPos().offset(ray.getFacing()).equals(pos))
                {
                    dumbRay = ray;
                    forceRay = ray;
                }

                for (EnumFacing facing : EnumFacing.values())
                {
                    BlockPos helpingPos = pos.offset(facing);
                    IBlockState state = HELPER.getBlockState(helpingPos);
                    if (!state.getMaterial().isReplaceable()
                            || quickEntityCheck(helpingPos))
                    {
                        continue;
                    }

                    Ray helpingRay = RayTraceFactory.fullTrace(
                                              entity, HELPER, helpingPos, -1.0);
                    if (helpingRay == null
                        || !helpingRay.getPos()
                                      .offset(helpingRay.getFacing())
                                      .equals(helpingPos)
                        || !helpingRay.isLegit()
                           && (smartRay.getValue() == RayTraceMode.Smart
                                || smartRay.getValue() == RayTraceMode.Force))
                    {
                        if (dumbRay == null
                            && helpingRay != null
                            && helpingRay.getPos()
                                         .offset(helpingRay.getFacing())
                                         .equals(helpingPos))
                        {
                            dumbHelpingRay = helpingRay;
                            setState(helpingPos);
                            dumbRay =
                                RayTraceFactory.rayTrace(entity,
                                                         helpingPos,
                                                         facing.getOpposite(),
                                                         HELPER,
                                                         state,
                                                         -1.0);
                            if (!dumbRay.getPos()
                                        .offset(dumbRay.getFacing())
                                        .equals(pos))
                            {
                                dumbRay = null;
                                dumbHelpingRay = null;
                            }

                            HELPER.delete(helpingPos);
                        }

                        continue;
                    }

                    setState(helpingPos);
                    ray = RayTraceFactory.rayTrace(entity,
                                                   helpingPos,
                                                   facing.getOpposite(),
                                                   HELPER,
                                                   state,
                                                   -1.0);
                    if (ray == null
                        || !ray.getPos().offset(ray.getFacing()).equals(pos))
                    {
                        continue;
                    }

                    if (forceRay == null)
                    {
                        forceRay = ray;
                        forceHelpingRay = helpingRay;
                    }

                    if (ray.isLegit()
                        || smartRay.getValue() != RayTraceMode.Smart
                            && smartRay.getValue() != RayTraceMode.Force)
                    {
                        if (entityCheck(helpingPos))
                        {
                            placeBlock(helpingRay.getPos(),
                                       helpingRay.getFacing(),
                                       helpingRay.getRotations(),
                                       helpingRay.getResult().hitVec);

                            if (blocksPlaced >= blocks.getValue()
                                 || noFastHelp(helpingPos, pos))
                            {
                                return true;
                            }

                            if (entityCheck(pos))
                            {
                                placeBlock(ray.getPos(),
                                           ray.getFacing(),
                                           ray.getRotations(),
                                           ray.getResult().hitVec);

                                setState(pos);
                                return blocksPlaced >= blocks.getValue()
                                        || rotate.getValue() == Rotate.Normal;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }

                    HELPER.delete(helpingPos);
                }
            }
            else if ((ray.isLegit()
                            || smartRay.getValue() != RayTraceMode.Smart
                                && smartRay.getValue() != RayTraceMode.Force)
                        && entityCheck(pos))
            {
                setState(pos);
                placeBlock(ray.getPos(),
                           ray.getFacing(),
                           ray.getRotations(),
                           ray.getResult().hitVec);

                return blocksPlaced >= blocks.getValue()
                        || rotate.getValue() == Rotate.Normal;
            }

            if (forceRay == null
                || !forceRay.getPos().offset(forceRay.getFacing()).equals(pos))
            {
                forceRay = dumbRay;
                forceHelpingRay = dumbHelpingRay;
            }

            if (smartRay.getValue() == RayTraceMode.Force
                    && forceRay != null
                    && forceRay.getPos()
                               .offset(forceRay.getFacing())
                               .equals(pos))
            {
                if (forceHelpingRay != null)
                {
                    BlockPos helping = forceHelpingRay
                            .getPos()
                            .offset(forceHelpingRay.getFacing());

                    if (entityCheck(helping))
                    {
                        placeBlock(forceHelpingRay.getPos(),
                                   forceHelpingRay.getFacing(),
                                   forceHelpingRay.getRotations(),
                                   forceHelpingRay.getResult().hitVec);

                        setState(helping);

                        if (blocksPlaced >= blocks.getValue()
                            || noFastHelp(helping, pos))
                        {
                            return true;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }

                BlockPos forcePos = forceRay.getPos()
                                            .offset(forceRay.getFacing());
                if (!entityCheck(forcePos))
                {
                    return false;
                }

                placeBlock(forceRay.getPos(),
                           forceRay.getFacing(),
                           forceRay.getRotations(),
                           forceRay.getResult().hitVec);

                setState(forcePos);
                return blocksPlaced >= blocks.getValue()
                        || rotate.getValue() == Rotate.Normal;
            }

            return false;
        }

        EnumFacing initialFacing = BlockUtil.getFacing(pos, HELPER);
        if (shouldHelp(initialFacing, pos))
        {
            BlockPos helpingPos = null;
            for (EnumFacing facing : EnumFacing.values())
            {
                helpingPos = pos.offset(facing);
                EnumFacing helpingFacing = BlockUtil.getFacing(helpingPos,
                                                               HELPER);
                if (helpingFacing != null)
                {
                    if (entityCheck(helpingPos))
                    {
                        initialFacing = facing;
                        placeBlock(helpingPos.offset(helpingFacing),
                                   helpingFacing.getOpposite());
                        setState(helpingPos);
                        break;
                    }
                }
            }

            if (blocksPlaced >= blocks.getValue()
                    || helpingPos != null && noFastHelp(helpingPos, pos))
            {
                return true;
            }
        }

        if (initialFacing == null || !entityCheck(pos))
        {
            return false;
        }

        placeBlock(pos.offset(initialFacing), initialFacing.getOpposite());
        setState(pos);
        return blocksPlaced >= blocks.getValue()
                || rotate.getValue() == Rotate.Normal;
    }

    /**
     * Helping method for {@link ObbyModule#placeBlock(BlockPos)}.
     *
     * @param facing the facing.
     * @param pos the position
     * @return <tt>true</tt> if additional calculations are
     *         required to help the position.
     */
    protected boolean shouldHelp(EnumFacing facing, BlockPos pos)
    {
        return facing == null;
    }

    /**
     * It's important that the {@link BlockPlacingModule#slot}
     * if set before this method is called!
     * <p></p>
     * Uses the {@link BlockStateHelper} to make it easier to
     * place on blocks that should/will exist on the server but
     * that we haven't been notified off yet.
     *
     * @param pos the position.
     */
    public void setState(BlockPos pos)
    {
        Block block = slot <= 0 || slot > 8  ? Blocks.ENDER_CHEST : null;
        if (block == null) // this should almost always be the case!
        {
            Item item = slot == -2 ? mc.player.getHeldItemOffhand()
                                              .getItem()
                                   : mc.player.inventory
                                              .getStackInSlot(slot)
                                              .getItem();
            if (item instanceof ItemBlock) // this should always be the case!
            {
                block = ((ItemBlock) item).getBlock();
            }
        }

        if (block != null)
        {
            HELPER.addBlockState(pos, block.getDefaultState());
        }
    }

    protected boolean quickEntityCheck(BlockPos pos)
    {
        return mc.world
                 .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))
                 .stream()
                 .anyMatch(e ->
                     e != null
                         && !EntityUtil.isDead(e)
                         && e.preventEntitySpawning
                         && !(e instanceof EntityEnderCrystal
                                 && attack.getValue()));
    }

    protected boolean noFastHelp(BlockPos helpingPos, BlockPos pos)
    {
        switch (fastHelpingBlocks.getValue())
        {
            case Off:
                return rotate.getValue() == Rotate.Normal;
            case Down:
                return rotate.getValue() == Rotate.Normal
                        && !pos.down().equals(helpingPos);
            default:
                return false;
        }
    }

}
