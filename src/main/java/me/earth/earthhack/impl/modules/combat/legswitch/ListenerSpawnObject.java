package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerSpawnObject extends
        ModuleListener<LegSwitch, PacketEvent.Receive<SPacketSpawnObject>>
{
    public ListenerSpawnObject(LegSwitch module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        EntityPlayerSP player = mc.player;
        if (module.instant.getValue()
            && player != null
            && Managers.SWITCH.getLastSwitch() >= module.coolDown.getValue()
            && !DamageUtil.isWeaknessed()
            && module.timer.passed(module.delay.getValue())
            && event.getPacket().getType() == 51)
        {
            SPacketSpawnObject packet = event.getPacket();
            LegConstellation constellation = module.constellation;
            if (constellation != null
                && !constellation.firstNeedsObby // TODO: make it place asnyc?
                && !constellation.secondNeedsObby
                && (InventoryUtil.isHolding(Items.END_CRYSTAL)
                    || module.autoSwitch.getValue() != LegAutoSwitch.None))
            {
                double x = packet.getX();
                double y = packet.getY();
                double z = packet.getZ();
                BlockPos pos = new BlockPos(x, y - 1, z);
                BlockPos previous = module.targetPos;
                if (!pos.equals(previous))
                {
                    return;
                }

                BlockPos targetPos = constellation.firstPos.equals(previous)
                                            ? constellation.secondPos
                                            : constellation.firstPos;
                EntityEnderCrystal entity =
                        new EntityEnderCrystal(mc.world, x, y, z);

                if (!module.rotate.getValue().noRotate(ACRotate.Break)
                        && !RotationUtil.isLegit(entity)
                    || !module.rotate.getValue().noRotate(ACRotate.Place)
                        && !RotationUtil.isLegit(targetPos))
                {
                    return;
                }

                RayTraceResult result =
                        RotationUtil.rayTraceTo(targetPos, mc.world);

                if (result == null)
                {
                    result = new RayTraceResult(
                            new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
                }

                entity.setUniqueId(packet.getUniqueId());
                entity.setEntityId(packet.getEntityID());
                entity.setShowBottom(false);

                int slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
                RayTraceResult finalResult = result;
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                {
                    int last = player.inventory.currentItem;
                    EnumHand hand = player.getHeldItemMainhand().getItem()
                                        == Items.END_CRYSTAL || slot != -2
                                    ? EnumHand.MAIN_HAND
                                    : EnumHand.OFF_HAND;

                    player.connection.sendPacket(
                            new CPacketUseEntity(entity));
                    player.connection.sendPacket(
                            new CPacketAnimation(EnumHand.MAIN_HAND));

                    InventoryUtil.switchTo(slot);

                    player.connection.sendPacket(
                        new CPacketPlayerTryUseItemOnBlock(
                            targetPos,
                            finalResult.sideHit,
                            hand,
                            (float) finalResult.hitVec.x,
                            (float) finalResult.hitVec.y,
                            (float) finalResult.hitVec.z));

                    player.connection.sendPacket(
                        new CPacketAnimation(hand));

                    if (last != slot
                        && module.autoSwitch.getValue() != LegAutoSwitch.Keep)
                    {
                        InventoryUtil.switchTo(last);
                    }
                });

                module.targetPos = targetPos;
                if (module.setDead.getValue())
                {
                    event.addPostEvent(() ->
                    {
                        if (mc.world != null)
                        {
                            Entity e =
                                mc.world.getEntityByID(packet.getEntityID());
                            if (e != null)
                            {
                                Managers.SET_DEAD.setDead(e);
                            }
                        }
                    });
                }

                module.timer.reset(module.delay.getValue());
            }
        }
    }

}
