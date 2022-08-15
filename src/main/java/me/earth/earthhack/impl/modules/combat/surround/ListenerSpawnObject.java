package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// TODO: handle weakness...
final class ListenerSpawnObject extends
        ModuleListener<Surround, PacketEvent.Receive<SPacketSpawnObject>>
{
    public ListenerSpawnObject(Surround module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        if (!module.predict.getValue()
            || module.rotate.getValue() == Rotate.Normal
            || Managers.SWITCH.getLastSwitch() < module.cooldown.getValue())
        {
            return;
        }

        SPacketSpawnObject packet = event.getPacket();
        if (packet.getType() != 51)
        {
            return;
        }

        EntityPlayer player = module.getPlayer();
        BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
        if (player.getDistanceSq(pos) < 9)
        {
            if (!module.async.getValue()
                    || DamageUtil.isWeaknessed()
                    || module.smartRay.getValue() != RayTraceMode.Fast
                    || !module.timer.passed(module.delay.getValue())
                    || !module.pop.getValue().shouldPop(
                            DamageUtil.calculate(pos.down(), player),
                            module.popTime.getValue()))
            {
                event.addPostEvent(() -> ListenerMotion.start(module));
                return;
            }

            try
            {
                placeAsync(packet, player);
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
    }

    private void placeAsync(SPacketSpawnObject packet, EntityPlayer player)
    {
        int slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN,
                                                 Blocks.ENDER_CHEST);
        if (slot == -1)
        {
            return;
        }

        AxisAlignedBB bb = new EntityEnderCrystal(mc.world,
                                                  packet.getX(),
                                                  packet.getY(),
                                                  packet.getZ())
                                .getEntityBoundingBox();

        Set<BlockPos> surrounding = module.createSurrounding(
                module.createBlocked(),
                Managers.ENTITIES.getPlayers());

        Map<BlockPos, EnumFacing> toPlace = new ConcurrentHashMap<>();
        for (BlockPos pos : surrounding)
        {
            if (bb.intersects(new AxisAlignedBB(pos))
                    && mc.world.getBlockState(pos)
                               .getMaterial()
                               .isReplaceable())
            {
                // TODO: smart raytrace here
                EnumFacing facing = BlockUtil.getFacing(pos, mc.world);
                if (facing != null)
                {
                    toPlace.put(pos.offset(facing), facing.getOpposite());
                }
            }
        }

        if (toPlace.isEmpty())
        {
            return;
        }

        List<BlockPos> placed = new ArrayList<>(
                Math.min(module.blocks.getValue(), toPlace.size()));

        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
        {
            int lastSlot = mc.player.inventory.currentItem;
            PacketUtil.attack(packet.getEntityID());
            InventoryUtil.switchTo(slot);
            boolean sneaking = mc.player.isSneaking();
            if (!sneaking)
            {
                PacketUtil.sneak(true);
            }

            int blocks = 0;
            for (Map.Entry<BlockPos, EnumFacing> entry : toPlace.entrySet())
            {
                float[] helpingRotations = RotationUtil.getRotations(
                        entry.getKey(), entry.getValue(), player);

                RayTraceResult result =
                    RayTraceUtil.getRayTraceResultWithEntity(
                        helpingRotations[0], helpingRotations[1], player);

                if (module.rotate.getValue() == Rotate.Packet)
                {
                    PingBypass.sendToActualServer(
                            new CPacketPlayer.Rotation(helpingRotations[0],
                                                       helpingRotations[1],
                                                       mc.player.onGround));
                }

                float[] f = RayTraceUtil.hitVecToPlaceVec(
                        entry.getKey(), result.hitVec);

                mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItemOnBlock(
                        entry.getKey(),
                        entry.getValue(),
                        InventoryUtil.getHand(slot),
                        f[0],
                        f[1],
                        f[2]));

                if (module.placeSwing.getValue() == PlaceSwing.Always)
                {
                    Swing.Packet.swing(InventoryUtil.getHand(slot));
                }

                placed.add(entry.getKey().offset(entry.getValue()));
                if (++blocks >= module.blocks.getValue())
                {
                    break;
                }
            }

            if (module.placeSwing.getValue() == PlaceSwing.Once)
            {
                Swing.Packet.swing(InventoryUtil.getHand(slot));
            }

            if (!sneaking)
            {
                PacketUtil.sneak(false);
            }

            InventoryUtil.switchTo(lastSlot);
        });

        module.timer.reset(module.delay.getValue());

        if (module.resync.getValue())
        {
            mc.addScheduledTask(() ->
            {
                module.placed.addAll(placed);
            });
        }
    }

}
