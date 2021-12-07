package me.earth.earthhack.impl.modules.combat.antitrap;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antitrap.util.AntiTrapMode;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static me.earth.earthhack.impl.util.helpers.blocks.ObbyModule.HELPER;

final class ListenerMotion extends ModuleListener<AntiTrap, MotionUpdateEvent>
{
    protected static final ModuleCache<Offhand> OFFHAND =
            Caches.getModule(Offhand.class);

    public ListenerMotion(AntiTrap module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (module.autoOff.getValue()
                && !PositionUtil.getPosition().equals(module.startPos))
        {
            module.disable();
            return;
        }

        switch (module.mode.getValue())
        {
            case Crystal:
                doCrystal(event);
                break;
            case FacePlace:
            case Fill:
                doObby(event, module.mode.getValue().getOffsets());
                break;
            default:
        }
    }

    private void doObby(MotionUpdateEvent event, Vec3i[] offsets)
    {
        if (event.getStage() == Stage.PRE)
        {
            // TODO: this looks very similar to AutoTrap,
            //  maybe its possible to abstract this even further?
            module.rotations = null;
            module.blocksPlaced = 0;

            for (BlockPos confirmed : module.confirmed)
            {
                module.placed.remove(confirmed);
            }

            module.placed.entrySet().removeIf(entry ->
                    System.currentTimeMillis() - entry.getValue()
                            >= module.confirm.getValue());

            BlockPos playerPos = PositionUtil.getPosition();
            BlockPos[] positions = new BlockPos[offsets.length];
            for (int i = 0; i < offsets.length; i++)
            {
                Vec3i offset = offsets[i];
                if (module.mode.getValue() == AntiTrapMode.Fill)
                {
                    if (mc.world.getBlockState(playerPos.add(offset.getX() / 2,
                                                             0,
                                                             offset.getZ() / 2))
                                .getBlock() == Blocks.BEDROCK)
                    {
                        continue;
                    }
                }

                positions[i] = playerPos.add(offset);
            }

            if (module.offhand.getValue())
            {
                if (!InventoryUtil.isHolding(Blocks.OBSIDIAN))
                {
                    module.previous = OFFHAND.returnIfPresent(Offhand::getMode,
                                                              null);
                    OFFHAND.computeIfPresent(o ->
                            o.setMode(OffhandMode.CRYSTAL));
                    return;
                }
            }
            else
            {
                module.slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
                if (module.slot == -1)
                {
                    ModuleUtil.disable(module, TextColor.RED
                            + "No Obsidian found.");
                    return;
                }
            }

            boolean done = true;
            List<BlockPos> toPlace = new LinkedList<>();
            for (BlockPos pos : positions)
            {
                if (pos == null)
                {
                    continue;
                }

                if (module.mode.getValue() == AntiTrapMode.Fill
                        && !module.highFill.getValue()
                        && pos.getY() > playerPos.getY())
                {
                    continue;
                }

                if (mc.world.getBlockState(pos).getMaterial().isReplaceable())
                {
                    toPlace.add(pos);
                    done = false;
                }
            }

            if (done)
            {
                module.disable();
                return;
            }

            boolean hasPlaced = false;
            Optional<BlockPos> crystalPos = toPlace
                    .stream()
                    .filter(pos ->
                            !mc.world.getEntitiesWithinAABB(
                                    EntityEnderCrystal.class,
                                    new AxisAlignedBB(pos)).isEmpty()
                                    && mc.world.getBlockState(pos)
                                    .getMaterial()
                                    .isReplaceable())
                    .findFirst();

            if (crystalPos.isPresent())
            {
                BlockPos pos = crystalPos.get();
                hasPlaced = module.placeBlock(pos);
            }

            // Only after here we need to use the Helper to get the BlockStates.
            if (!hasPlaced)
            {
                for (BlockPos pos : toPlace)
                {
                    if (!module.placed.containsKey(pos)
                            && HELPER.getBlockState(pos)
                                     .getMaterial()
                                     .isReplaceable())
                    {
                        module.confirmed.remove(pos);
                        if (module.placeBlock(pos))
                        {
                            break;
                        }
                    }
                }
            }

            if (module.rotate.getValue() != Rotate.None)
            {
                if (module.rotations != null)
                {
                    event.setYaw(module.rotations[0]);
                    event.setPitch(module.rotations[1]);
                }
            }
            else
            {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, module::execute);
            }
        }
        else
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, module::execute);
        }
    }

    private void doCrystal(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            List<BlockPos> positions = module.getCrystalPositions();

            if (positions.isEmpty() || !module.isEnabled())
            {
                if (!module.empty.getValue())
                {
                    module.disable();
                }

                return;
            }

            if (module.offhand.getValue())
            {
                if (!InventoryUtil.isHolding(Items.END_CRYSTAL))
                {
                    module.previous = OFFHAND.returnIfPresent(Offhand::getMode,
                                                              null);
                    OFFHAND.computeIfPresent(o ->
                            o.setMode(OffhandMode.CRYSTAL));
                    return;
                }
            }
            else
            {
                module.slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);

                if (module.slot == -1)
                {
                    ModuleUtil.disable(module, TextColor.RED
                                                + "No crystals found.");
                    return;
                }
            }

            EntityPlayer closest = EntityUtil.getClosestEnemy();
            if (closest != null)
            {
                positions.sort(Comparator.comparingDouble(pos ->
                        BlockUtil.getDistanceSq(closest, pos)));
            }

            // get last, furthest away, pos.
            module.pos = positions.get(positions.size() - 1);
            module.rotations = RotationUtil.getRotationsToTopMiddle(module.pos.up());
            module.result = RayTraceUtil.getRayTraceResult(module.rotations[0],
                                                           module.rotations[1],
                                                           3.0f);
            if (module.rotate.getValue() == Rotate.Normal)
            {
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);
            }
            else
            {
                executeCrystal();
            }
        }
        else
        {
            executeCrystal();
        }
    }

    private void executeCrystal()
    {
        if (module.pos != null && module.result != null)
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, this::executeLocked);
        }
    }

    private void executeLocked()
    {
        final int lastSlot = mc.player.inventory.currentItem;
        if (!InventoryUtil.isHolding(Items.END_CRYSTAL))
        {
            if (module.offhand.getValue() || module.slot == -1)
            {
                return;
            }
            else
            {
                InventoryUtil.switchTo(module.slot);
            }
        }

        EnumHand hand =
                mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL
                        ? EnumHand.OFF_HAND
                        : EnumHand.MAIN_HAND;

        CPacketPlayerTryUseItemOnBlock place =
                new CPacketPlayerTryUseItemOnBlock(
                        module.pos,
                        module.result.sideHit,
                        hand,
                        (float) module.result.hitVec.x,
                        (float) module.result.hitVec.y,
                        (float) module.result.hitVec.z);

        CPacketAnimation swing = new CPacketAnimation(hand);

        if (module.rotate.getValue() == Rotate.Packet
                && module.rotations != null)
        {
            mc.player.connection.sendPacket(
                    new CPacketPlayer.Rotation(
                            module.rotations[0],
                            module.rotations[1],
                            mc.player.onGround));
        }

        mc.player.connection.sendPacket(place);
        mc.player.connection.sendPacket(swing);

        InventoryUtil.switchTo(lastSlot);

        if (module.swing.getValue())
        {
            Swing.Client.swing(hand);
        }

        module.disable();
    }

}
