package me.earth.earthhack.impl.modules.player.suicide;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

final class ListenerMotion extends ModuleListener<Suicide, MotionUpdateEvent>
{
    public ListenerMotion(Suicide module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (module.displaying)
        {
            return;
        }

        if (mc.player.getHealth() <= 0.0f)
        {
            module.disable();
            return;
        }

        if (module.mode.getValue() == SuicideMode.Command)
        {
            NetworkUtil.sendPacketNoEvent(new CPacketChatMessage("/kill"));
            module.disable();
            return;
        }

        if (module.throwAwayTotem.getValue()
            && InventoryUtil.validScreen()
            && module.timer.passed(module.throwDelay.getValue())
            && mc.player.getHeldItemOffhand().getItem()
                == Items.TOTEM_OF_UNDYING)
        {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                mc.playerController.windowClick(
                    0, 45, 1, ClickType.THROW, mc.player));
            module.timer.reset();
        }

        int slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
        if (slot == -1)
        {
            ModuleUtil.disableRed(module, "No Crystals found!");
            return;
        }

        if (event.getStage() == Stage.PRE)
        {
            module.result  = null;
            module.pos     = null;
            module.crystal = null;
            if (module.breakTimer.passed(module.breakDelay.getValue()))
            {
                Entity crystal = null;
                float maxDamage = Float.MIN_VALUE;
                for (Entity entity : mc.world.loadedEntityList)
                {
                    if (!entity.isDead
                        && entity instanceof EntityEnderCrystal
                        && RotationUtil.getRotationPlayer()
                                       .getDistanceSq(entity)
                            < MathUtil.square(module.breakRange.getValue())
                        && (RotationUtil.getRotationPlayer()
                                        .canEntityBeSeen(entity)
                            || RotationUtil.getRotationPlayer()
                                           .getDistanceSq(entity)
                                < MathUtil.square(module.trace.getValue())))
                    {
                        float damage = DamageUtil.calculate(entity);
                        if (damage > maxDamage)
                        {
                            maxDamage = damage;
                            crystal = entity;
                        }
                    }
                }

                if (crystal != null)
                {
                    module.crystal = crystal;
                    if (module.rotate.getValue())
                    {
                        float[] rotations = RotationUtil.getRotations(crystal);
                        event.setYaw(rotations[0]);
                        event.setPitch(rotations[1]);
                    }

                    return;
                }
            }

            if (!module.placeTimer.passed(module.placeDelay.getValue()))
            {
                return;
            }

            float maxDamage = Float.MIN_VALUE;
            BlockPos middle = PositionUtil.getPosition();
            int x = middle.getX();
            int y = middle.getY();
            int z = middle.getZ();
            int maxRadius = Sphere.getRadius(6.0);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockPos bestPos = null;
            for (int i = 1; i < maxRadius; i++)
            {
                Vec3i v = Sphere.get(i);
                pos.setPos(x + v.getX(), y + v.getY(), z + v.getZ());
                if (!BlockUtil.canPlaceCrystal(
                        pos, false, module.newVer.getValue(),
                        mc.world.loadedEntityList,
                        module.newVerEntities.getValue(), 0)
                    || !BlockUtil.isCrystalPosInRange(pos,
                        module.placeRange.getValue(),
                        module.placeRange.getValue(),
                        module.trace.getValue()))
                {
                    continue;
                }

                float damage = DamageUtil.calculate(pos);
                if (damage > maxDamage)
                {
                    maxDamage = damage;
                    bestPos = pos.toImmutable();
                }
            }

            if (bestPos != null)
            {
                Ray result = RayTraceFactory.fullTrace(
                    RotationUtil.getRotationPlayer(), mc.world, bestPos, -1.0);
                if (result == null)
                {
                    return;
                }

                if (module.rotate.getValue())
                {
                    event.setYaw(result.getRotations()[0]);
                    event.setPitch(result.getRotations()[1]);
                }

                module.pos = bestPos;
                module.result = result.getResult();
            }
        }
        else if (event.getStage() == Stage.POST)
        {
            if (module.crystal != null)
            {
                mc.player.connection.sendPacket(
                    new CPacketUseEntity(module.crystal));
                mc.player.connection.sendPacket(
                    new CPacketAnimation(EnumHand.MAIN_HAND));
                module.breakTimer.reset();
                return;
            }

            if (module.pos != null && module.result != null)
            {
                float[] r = RayTraceUtil.hitVecToPlaceVec(module.pos,
                                                          module.result.hitVec);
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                {
                    int last = mc.player.inventory.currentItem;
                    InventoryUtil.switchTo(slot);

                    mc.player.connection.sendPacket(
                        new CPacketPlayerTryUseItemOnBlock(
                            module.pos,
                            module.result.sideHit,
                            InventoryUtil.getHand(slot),
                            r[0], r[1], r[2]));

                    mc.player.connection.sendPacket(
                        new CPacketAnimation(InventoryUtil.getHand(slot)));

                    if (module.silent.getValue())
                    {
                        InventoryUtil.switchTo(last);
                    }
                });

                module.placed.add(module.pos);
                module.placeTimer.reset();
            }
        }
    }

}
