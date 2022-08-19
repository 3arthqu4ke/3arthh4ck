package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonStage;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion extends ModuleListener<PistonAura, MotionUpdateEvent>
{
    private static final ModuleCache<NoGlitchBlocks> NO_GLITCH_BLOCKS =
            Caches.getModule(NoGlitchBlocks.class);

    public ListenerMotion(PistonAura module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            module.clicked.clear();
            module.blocksPlaced = 0;
            module.rotations = null;
            module.pistonSlot = InventoryUtil.findHotbarBlock(
                                                        Blocks.PISTON,
                                                        Blocks.STICKY_PISTON);
            if (module.pistonSlot == -1
                    && shouldDisable(PistonStage.PISTON))
            {
                module.disableWithMessage("<"
                                + module.getDisplayName()
                                + "> "
                                + TextColor.RED
                                + "No Pistons found!");
                return;
            }

            module.redstoneSlot = InventoryUtil.findHotbarBlock(
                                                        Blocks.REDSTONE_BLOCK,
                                                        Blocks.REDSTONE_TORCH);
            if (module.redstoneSlot == -1
                    && shouldDisable(PistonStage.REDSTONE))
            {
                module.disableWithMessage("<"
                        + module.getDisplayName()
                        + "> "
                        + TextColor.RED
                        + "No Redstone found!");
                return;
            }

            module.crystalSlot = InventoryUtil.findHotbarItem(
                                                        Items.END_CRYSTAL);
            if (module.crystalSlot == -1
                    && shouldDisable(PistonStage.CRYSTAL))
            {
                module.disableWithMessage("<"
                        + module.getDisplayName()
                        + "> "
                        + TextColor.RED
                        + "No Crystals found!");
                return;
            }

            if (module.reset
                    && module.nextTimer.passed(module.next.getValue())
                    && module.current != null)
            {
                module.current.setValid(false);
            }

            if (module.current == null || !module.current.isValid())
            {
                module.current = module.findTarget();
                if (module.current == null || !module.current.isValid())
                {
                    return;
                }
            }

            module.stage = module.current.getOrder()[module.index];
            while (module.index < 3 && module.stage == null)
            {
                module.index++;
                module.stage = module.current.getOrder()[module.index];
            }

            while (module.blocksPlaced < module.blocks.getValue())
            {
                if (!runPre()
                        || module.rotations != null
                            && module.rotate.getValue() == Rotate.Normal)
                {
                    break;
                }
            }

            if (module.blocksPlaced > 0)
            {
                module.timer.reset(module.delay.getValue());
            }

            if (module.rotations != null)
            {
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);
            }
        }
        else
        {
            if (module.current == null || !module.current.isValid())
            {
                return;
            }

            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                module.slot = mc.player.inventory.currentItem;

                boolean sneak = module.blocksPlaced == 0
                        || module.actions.isEmpty()
                        || module.smartSneak.getValue()
                            && !(Managers.ACTION.isSneaking()
                                || module.clicked.stream().anyMatch(b ->
                                    SpecialBlocks.shouldSneak(b, false)));
                if (!sneak)
                {
                    PingBypass.sendToActualServer(
                            new CPacketEntityAction(mc.player,
                                    CPacketEntityAction.Action.START_SNEAKING));
                }

                CollectionUtil.emptyQueue(module.actions);

                if (!sneak)
                {
                    PingBypass.sendToActualServer(
                            new CPacketEntityAction(mc.player,
                                    CPacketEntityAction.Action.STOP_SNEAKING));
                }

                if (mc.player.inventory.currentItem != module.slot)
                {
                    InventoryUtil.switchTo(module.slot);
                }
            });
        }
    }

    private boolean runPre()
    {
        module.stage = module.current.getOrder()[module.index];
        BlockPos pos = module.stage.getPos(module.current);

        if (module.stage == PistonStage.BREAK)
        {
            if (!module.explode.getValue())
            {
                return false;
            }

            for (EntityEnderCrystal crystal : mc.world
                    .getEntitiesWithinAABB(EntityEnderCrystal.class,
                                            new AxisAlignedBB(pos)))
            {
                if (crystal.getPosition().equals(pos.up())
                        || crystal.getPosition().equals(
                                module.current.getStartPos().up()))
                {
                    float[] crystalRots = RotationUtil.getRotations(crystal);
                    CPacketPlayer rotation = null;
                    if (module.rotate.getValue() == Rotate.Packet
                            && module.rotations != null)
                    {
                        rotation =
                                new CPacketPlayer.Rotation(crystalRots[0],
                                                           crystalRots[1],
                                                           mc.player.onGround);
                    }
                    else if (module.rotate.getValue() != Rotate.None)
                    {
                        module.rotations = crystalRots;
                    }

                    CPacketPlayer finalRotation = rotation;
                    module.actions.add(() ->
                    {
                        if (module.breakTimer
                                  .passed(module.breakDelay.getValue())
                                    && Managers.SWITCH.getLastSwitch() >=
                                        module.coolDown.getValue())
                        {
                            if (finalRotation != null)
                            {
                                PingBypass.sendToActualServer(finalRotation);
                            }

                            PingBypass.sendToActualServer(
                                    new CPacketUseEntity(crystal));
                            PingBypass.sendToActualServer(
                                    new CPacketAnimation(EnumHand.MAIN_HAND));
                            module.breakTimer.reset();

                            module.nextTimer.reset();
                            module.reset = true;
                        }
                    });

                    return false;
                }
            }

            return false;
        }

        if (!module.timer.passed(module.delay.getValue()))
        {
            return false;
        }

        if (module.stage == PistonStage.CRYSTAL)
        {
            for (Entity entity : mc.world
                    .getEntitiesWithinAABB(Entity.class,
                            new AxisAlignedBB(pos.up(),
                                              module.current.getStartPos())))
            {
                if (entity == null || EntityUtil.isDead(entity))
                {
                    continue;
                }

                if (entity instanceof EntityEnderCrystal
                        && entity.getPosition().equals(pos.up()))
                {
                    module.index++;
                    module.stage = module.current.getOrder()[module.index];
                    pos = module.stage.getPos(module.current);
                    break;
                }

                module.current.setValid(false);
                return false;
            }
        }

        //TODO: Use BlockPlacingModule#placeBlock here, its almost the same!
        if (pos != null)
        {
            EnumFacing facing = BlockUtil.getFacing(pos);
            if (facing == null
                    && module.stage != PistonStage.CRYSTAL
                    && (!module.packet.getValue()
                        || module.packetTimer
                                .passed(module.confirmation.getValue()))
                    || module.stage != PistonStage.CRYSTAL
                        && module.checkEntities(pos))
            {
                module.current.setValid(false);
                return false;
            }

            if (facing == null && module.stage != PistonStage.CRYSTAL)
            {
                return false;
            }

            int slot = module.getSlot();
            if (slot == -1)
            {
                module.disableWithMessage("<"
                        + module.getDisplayName()
                        + "> "
                        + TextColor.RED
                        + "Items missing!");
                return false;
            }

            module.actions.add(() -> InventoryUtil.switchTo(slot));

            float[] rotations;
            if (module.stage == PistonStage.CRYSTAL)
            {
                RayTraceResult result;
                if (module.rotate.getValue() != Rotate.None)
                {
                    rotations = RotationUtil.getRotationsToTopMiddle(pos.up());
                    result = RayTraceUtil.getRayTraceResult(
                                                rotations[0],
                                                rotations[1],
                                                module.placeRange.getValue());
                }
                else
                {
                    result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5),
                                                EnumFacing.UP);
                }

                // Opposite because tge PlacePacket gets the Opposite
                facing = result.sideHit.getOpposite();
                rotations = RotationUtil.getRotationsToTopMiddle(pos.up());
            }
            else
            {
                assert facing != null;
                rotations = RotationUtil
                        .getRotations(pos.offset(facing), facing.getOpposite());
            }

            EnumHand hand = slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

            if (module.stage == PistonStage.PISTON
                    && module.multiDirectional.getValue())
            {
                EnumFacing toFace = module.current.getFacing().getOpposite();
                EnumFacing piston = module.getFacing(pos, rotations);
                if (piston == EnumFacing.UP || piston == EnumFacing.DOWN)
                {
                    module.current.setValid(false);
                    return false;
                }

                int index = 0;
                while (piston != toFace && index < 36)
                {
                    rotations[0] = (rotations[0] + 10) % 360.0f;
                    piston = module.getFacing(pos, rotations);
                    index++;
                }

                if (piston != toFace)
                {
                    return false;
                }
            }

            switch (module.rotate.getValue())
            {
                case None:
                    if (module.stage != PistonStage.PISTON)
                    {
                        break;
                    }
                case Normal:
                    if (module.rotations == null)
                    {
                        module.rotations = rotations;
                    }
                    else
                    {
                        return false;
                    }
                    break;
                case Packet:
                    CPacketPlayer rotation =
                            new CPacketPlayer.Rotation(rotations[0],
                                                       rotations[1],
                                                       mc.player.onGround);
                    module.actions.add(() ->
                           PingBypass.sendToActualServer(rotation));
                    break;
                default:
            }

            RayTraceResult result = RayTraceUtil.getRayTraceResult(
                                                                rotations[0],
                                                                rotations[1]);

            float[] f = RayTraceUtil.hitVecToPlaceVec(pos.offset(facing),
                                                      result.hitVec);

            BlockPos on = module.stage == PistonStage.CRYSTAL
                                ? pos
                                : pos.offset(facing);

            module.clicked.add(mc.world.getBlockState(on).getBlock());

            CPacketPlayerTryUseItemOnBlock place =
                    new CPacketPlayerTryUseItemOnBlock(
                            on,
                            facing.getOpposite(),
                            hand,
                            f[0],
                            f[1],
                            f[2]);

            if (module.stage != PistonStage.CRYSTAL
                    && !module.packet.getValue()
                    && (!NO_GLITCH_BLOCKS.isPresent()
                            || !NO_GLITCH_BLOCKS.get().noPlace()))
            {
                ItemStack stack = slot == -2
                                    ? mc.player.getHeldItemOffhand()
                                    : mc.player.inventory.getStackInSlot(slot);

                module.placeClient(stack,
                        on,
                        InventoryUtil.getHand(slot),
                        facing,
                        f[0],
                        f[1],
                        f[2]);
            }

            module.actions.add(() ->
            {
                PingBypass.sendToActualServer(place);
                PingBypass.sendToActualServer(new CPacketAnimation(hand));

                if (module.swing.getValue())
                {
                    Swing.Client.swing(hand);
                }

                module.packetTimer.reset();
            });

            module.blocksPlaced++;
            module.index = module.index == 4 ? 4 : module.index + 1;
            return module.rotate.getValue() != Rotate.Normal;
        }

        return false;
    }

    /**
     * If we are missing an item this method checks if we are
     * going to need that item and if we do returns <tt>true</tt>.
     *
     * @param missing the pistonStage we dont have an item for.
     * @return <tt>true</tt> if theres a pistonStage left that needs
     *          the missing one.
     */
    private boolean shouldDisable(PistonStage missing)
    {
        if (module.current == null || !module.current.isValid())
        {
            return true;
        }

        if (module.stage == PistonStage.BREAK)
        {
            return false;
        }

        for (int i = module.index; i < 4; i++)
        {
            if (module.current.getOrder()[i] == missing)
            {
                return true;
            }
        }

        return false;
    }

}
