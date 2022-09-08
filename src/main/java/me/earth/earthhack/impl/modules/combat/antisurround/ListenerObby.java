package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperLiquids;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

final class ListenerObby extends ObbyListener<AntiSurround>
{
    private BlockPos crystalPos = null;

    public ListenerObby(AntiSurround module)
    {
        super(module, EventBus.DEFAULT_PRIORITY);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void invoke(MotionUpdateEvent event)
    {
        if (!module.async.getValue() && !module.normal.getValue())
        {
            module.reset();
            return;
        }

        crystalPos = null;
        if (AntiSurround.LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false))
        {
            if (module.active.get() || module.semiActive.get())
            {
                synchronized (module)
                {
                    module.reset();
                }
            }

            return;
        }

        synchronized (module)
        {
            if (module.active.get())
            {
                EntityPlayer target = module.target;
                if (target == null || EntityUtil.isDead(target))
                {
                    module.reset();
                    return;
                }

                IBlockState state;
                if (!(state = mc.world
                                .getBlockState(PositionUtil.getPosition(target)))
                                .getMaterial()
                                .isReplaceable()
                        && state.getBlock() // burrow
                                .getExplosionResistance(mc.player) > 100)
                {
                    module.reset();
                    return;
                }

                if (module.pos == null)
                {
                    module.reset();
                    return;
                }

                if (module.stopOnObby.getValue()
                    && mc.world.getBlockState(module.pos)
                               .getBlock() == Blocks.OBSIDIAN)
                {
                    module.reset();
                    return;
                }

                IBlockStateHelper helper = new BlockStateHelper();
                helper.addAir(module.pos);
                float damage = DamageUtil.calculate(module.pos, target, helper);
                if (damage < module.minDmg.getValue())
                {
                    module.reset();
                    return;
                }
            }
            else if (module.semiActive.get()
                    && System.nanoTime() - module.semiActiveTime
                        > TimeUnit.MILLISECONDS.toNanos(15))
            {
                module.semiActive.set(false);
            }
        }

        if (!module.active.get()
            && event.getStage() == Stage.PRE
            && module.persistent.getValue()
            && !module.holdingCheck())
        {
            MineSlots mine = HelperLiquids.getSlots(module.onGround.getValue());
            if (mine.getBlockSlot() == -1
                || mine.getToolSlot() == -1
                || mine.getDamage() < module.minMine.getValue()
                    && !(module.isAnvil = module.anvilCheck(mine)))
            {
                return;
            }

            int crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
            if (crystalSlot == -1)
            {
                return;
            }

            int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
            for (EntityPlayer player : mc.world.playerEntities)
            {
                if (player == null
                    || EntityUtil.isDead(player)
                    || player.equals(mc.player)
                    || player.equals(RotationUtil.getRotationPlayer())
                    || Managers.FRIENDS.contains(player)
                    || player.getDistanceSq(RotationUtil.getRotationPlayer())
                            > MathUtil.square(module.range.getValue() + 2))
                {
                    continue;
                }

                BlockPos playerPos = PositionUtil.getPosition(player);
                for (EnumFacing facing : EnumFacing.HORIZONTALS)
                {
                    BlockPos pos = playerPos.offset(facing);
                    if (BlockUtil.getDistanceSq(pos)
                        > MathUtil.square(module.range.getValue()))
                    {
                        continue;
                    }

                    BlockPos down = pos.offset(facing).down();
                    if (BlockUtil.getDistanceSq(down)
                        > MathUtil.square(module.range.getValue()))
                    {
                        continue;
                    }

                    Entity blocking = module.getBlockingEntity(
                            pos, mc.world.loadedEntityList);
                    if (blocking != null
                        && !(blocking instanceof EntityEnderCrystal))
                    {
                        continue;
                    }

                    IBlockState state = mc.world.getBlockState(pos);
                    if (state.getMaterial().isReplaceable()
                        || state.getBlock() == Blocks.BEDROCK
                        || state.getBlock() == Blocks.OBSIDIAN
                        || state.getBlock() == Blocks.ENDER_CHEST)
                    {
                        continue;
                    }

                    int slot = MineUtil.findBestTool(playerPos, state);
                    double damage = MineUtil.getDamage(state,
                        mc.player.inventory.getStackInSlot(slot), playerPos,
                        RotationUtil.getRotationPlayer().onGround);

                    if (damage < module.minMine.getValue())
                    {
                        continue;
                    }

                    if (BlockUtil.canPlaceCrystalReplaceable(down,
                            true, module.newVer.getValue(),
                            mc.world.loadedEntityList,
                            module.newVerEntities.getValue(), 0))
                    {
                        IBlockState dState = mc.world.getBlockState(down);
                        if ((!module.obby.getValue() || obbySlot == -1)
                            && dState.getBlock() != Blocks.OBSIDIAN
                            && dState.getBlock() != Blocks.BEDROCK)
                        {
                            continue;
                        }

                        BlockPos on = null;
                        EnumFacing onFacing = null;
                        for (EnumFacing off : EnumFacing.values())
                        {
                            on = pos.offset(off);
                            if (BlockUtil.getDistanceSq(on)
                                <= MathUtil.square(module.range.getValue())
                                    && !mc.world.getBlockState(on)
                                                .getMaterial()
                                                .isReplaceable())
                            {
                                onFacing = off.getOpposite();
                                break;
                            }
                        }

                        if (onFacing == null) // TODO: helping blocks?
                        {
                            continue;
                        }

                        synchronized (module)
                        {
                            if (!module.isActive())
                            {
                                module.semiPos = null;
                            }

                            if (module.placeSync(pos, down, on, onFacing,
                                obbySlot, mine, crystalSlot, blocking,
                                player, false))
                            {
                                // override toolSlot to best slot for block
                                module.toolSlot = slot;
                                module.mine     = true;
                                if (module.rotations != null
                                    && module.rotate.getValue()
                                        != Rotate.None)
                                {
                                    setRotations(module.rotations, event);
                                }
                                else
                                {
                                    module.execute();
                                }
                            }
                        }

                        return;
                    }
                }
            }
        }

        synchronized (module)
        {
            if (!module.active.get())
            {
                if (module.semiActive.get()
                    && System.nanoTime() - module.semiActiveTime
                        > TimeUnit.MILLISECONDS.toNanos(15))
                {
                    module.semiActive.set(false);
                }

                return;
            }

            if (module.holdingCheck())
            {
                module.reset();
                return;
            }

            super.invoke(event);
        }
    }

    @Override
    protected boolean updatePlaced()
    {
        super.updatePlaced();
        if (module.pos == null || module.crystalPos == null)
        {
            module.reset();
        }

        // dont do anything if module is not active
        return !module.active.get();
    }

    @Override
    protected boolean hasTimerNotPassed()
    {
        boolean result = super.hasTimerNotPassed();
        if (module.isAnvil && module.pos != null)
        {
            if (!module.hasMined)
            {
                mine(module.pos);
                return false;
            }
            else if (++module.ticks < 4)
            {
                return false;
            }

            if (!result)
            {
                return false;
            }
        }

        return result;
    }

    // TODO: put this on execute so we can rotate...
    private void mine(BlockPos pos)
    {
        EnumFacing facing = RayTraceUtil.getFacing(
                RotationUtil.getRotationPlayer(), pos, true);

        PacketUtil.startDigging(pos, facing);
        if (module.digSwing.getValue())
        {
            Swing.Packet.swing(EnumHand.MAIN_HAND);
        }

        module.hasMined = true;
        module.ticks = 0;
    }

    @Override
    protected int getSlot()
    {
        module.obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        MineSlots slots = HelperLiquids.getSlots(module.onGround.getValue());
        if (slots.getDamage() < module.minMine.getValue()
                && !(module.isAnvil = module.anvilCheck(slots))
            || slots.getToolSlot() == -1
            || slots.getBlockSlot() == -1)
        {
            module.reset();
            return -1;
        }

        module.crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
        if (module.crystalSlot == -1)
        {
            module.reset();
            return -1;
        }

        module.toolSlot = slots.getToolSlot();
        return slots.getBlockSlot();
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        BlockPos pos = module.pos;
        BlockPos crystalPos = module.crystalPos;
        if (pos == null || crystalPos == null)
        {
            result.setValid(false);
            return result;
        }

        if (mc.world.getBlockState(pos).getMaterial().isReplaceable())
        {
            AntiSurround.HELPER.addBlockState(pos,
                                              Blocks.AIR.getDefaultState());
            result.getTargets().add(pos);
        }
        else if (entityCheck(pos))
        {
            AntiSurround.HELPER.addBlockState(pos,
                                              Blocks.AIR.getDefaultState());
            module.mine = true;
            result.getTargets().add(pos);
        }
        else
        {
            placeObby(crystalPos, result);
        }

        return result;
    }

    @Override
    protected void disableModule()
    {
        module.reset();
    }

    @Override
    protected boolean rotateCheck()
    {
        if (crystalPos != null
                && (!module.isAnvil || module.ticks > 3 && module.hasMined))
        {
            IBlockStateHelper helper = new BlockStateHelper();
            helper.addBlockState(crystalPos, Blocks.OBSIDIAN.getDefaultState());
            RayTraceResult ray = null;
            if (module.rotations != null)
            {
                ray = RotationUtil.rayTraceWithYP(crystalPos, helper,
                        module.rotations[0], module.rotations[1],
                        (b, p) -> p.equals(crystalPos));
            }

            if (ray == null)
            {
                double x = RotationUtil.getRotationPlayer().posX;
                double y = RotationUtil.getRotationPlayer().posY;
                double z = RotationUtil.getRotationPlayer().posZ;
                module.rotations = RotationUtil.getRotations(
                                crystalPos.getX() + 0.5f,
                                crystalPos.getY() + 1,
                                crystalPos.getZ() + 0.5f,
                                x, y, z,
                                mc.player.getEyeHeight());

                ray = RotationUtil.rayTraceWithYP(crystalPos, helper,
                    module.rotations[0], module.rotations[1],
                    (b, p) -> p.equals(crystalPos));

                if (ray == null)
                {
                    ray = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5),
                                                EnumFacing.UP,
                                                crystalPos);
                }
            }

            int crystalSlot = module.crystalSlot;
            RayTraceResult finalResult = ray;
            float[] f = RayTraceUtil.hitVecToPlaceVec(crystalPos, ray.hitVec);
            EnumHand h = InventoryUtil.getHand(crystalSlot);
            BlockPos finalPos = crystalPos;
            module.post.add(() ->
            {
                module.crystalSwitchBackSlot = crystalSlot;
                module.cooldownBypass.getValue().switchTo(crystalSlot);
                mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItemOnBlock(
                        finalPos, finalResult.sideHit, h, f[0], f[1], f[2]));
            });
        }

        return super.rotateCheck();
    }

    private void placeObby(BlockPos crystalPos, TargetResult result)
    {
        if (module.crystalSlot == -1)
        {
            module.reset();
            result.setValid(false);
            return;
        }

        List<Entity> entities = mc.world.loadedEntityList;
        if (!module.attackTimer.passed(module.itemDeathTime.getValue()))
        {
            entities = entities.stream()
                               .filter(e -> !(e instanceof EntityItem))
                               .collect(Collectors.toList());
        }

        if (!BlockUtil.canPlaceCrystalReplaceable(crystalPos,
                true, module.newVer.getValue(), entities,
                module.newVerEntities.getValue(), 0))
        {
            module.reset();
            result.setValid(false);
            return;
        }

        IBlockState state = mc.world.getBlockState(crystalPos);
        if (state.getBlock() != Blocks.OBSIDIAN
            && state.getBlock() != Blocks.BEDROCK)
        {
            if (!state.getMaterial().isReplaceable()
                || !module.obby.getValue()
                || module.obbySlot == -1)
            {
                module.reset();
                result.setValid(false);
                return;
            }

            result.getTargets().add(crystalPos);
            module.slot = module.obbySlot;
        }

        this.crystalPos = crystalPos;
    }

    private boolean entityCheck(BlockPos pos)
    {
        BlockPos boost1 = pos.up();
        for (Entity entity : mc.world.getEntitiesWithinAABB(
                                                    EntityEnderCrystal.class,
                                                    new AxisAlignedBB(boost1)))
        {
            if (entity == null || EntityUtil.isDead(entity))
            {
                continue;
            }

            return true;
        }

        return false;
    }

}
