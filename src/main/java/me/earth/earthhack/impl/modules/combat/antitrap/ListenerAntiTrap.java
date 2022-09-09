package me.earth.earthhack.impl.modules.combat.antitrap;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antitrap.util.AntiTrapMode;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Comparator;
import java.util.List;

final class ListenerAntiTrap extends ObbyListener<AntiTrap>
{
    private static final ModuleCache<Offhand> OFFHAND =
        Caches.getModule(Offhand.class);

    public AntiTrapMode mode = AntiTrapMode.Fill;

    public ListenerAntiTrap(AntiTrap module)
    {
        super(module, 10);
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

        this.mode = module.mode.getValue();
        switch (this.mode)
        {
            case Crystal:
                doCrystal(event);
                break;
            case FacePlace:
            case Bomb:
            case Fill:
                super.invoke(event);
                break;
            default:
        }
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        BlockPos playerPos = PositionUtil.getPosition();
        Vec3i[] offsets = mode.getOffsets();
        for (Vec3i offset : offsets)
        {
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

            BlockPos pos = playerPos.add(offset);
            if (module.mode.getValue() == AntiTrapMode.Fill
                    && !module.highFill.getValue()
                    && pos.getY() > playerPos.getY()
                || module.mode.getValue() == AntiTrapMode.FacePlace
                    && !module.highFacePlace.getValue()
                    && pos.getY() > playerPos.getY() + 1)
            {
                continue;
            }

            result.getTargets().add(pos);
        }

        return result;
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
                positions.sort(Comparator.comparingDouble(
                    pos -> BlockUtil.getDistanceSq(closest, pos)));
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
            PingBypass.sendToActualServer(
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
