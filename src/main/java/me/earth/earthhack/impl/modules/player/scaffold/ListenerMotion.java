package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

final class ListenerMotion extends ModuleListener<Scaffold, MotionUpdateEvent>
{
    private static final BlockStateHelper HELPER = new BlockStateHelper();
    private static final ModuleCache<Freecam> FREECAM =
            Caches.getModule(Freecam.class);
    private static final ModuleCache<Spectate> SPECTATE =
            Caches.getModule(Spectate.class);

    public ListenerMotion(Scaffold module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (FREECAM.isEnabled() && !module.freecam.getValue()
                || SPECTATE.isEnabled() && !module.spectate.getValue())
        {
            return;
        }

        if (module.aac.getValue()
                && module.aacTimer.passed(module.aacDelay.getValue())
                && mc.player.onGround)
        {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
            module.aacTimer.reset();
        }

        if (event.getStage() == Stage.PRE)
        {
            module.facing = null;
            BlockPos prev = module.pos;
            module.pos    = null;

            module.pos = module.findNextPos();
            if (module.pos != null)
            {
                module.rot = module.pos;
                if (!module.pos.equals(prev))
                {
                    module.rotationTimer.reset();
                }

                setRotations(module.pos, event);
            }
            else if (module.rot != null
                    && module.rotate.getValue()
                    && module.keepRotations.getValue() != 0
                    && !module.rotationTimer.passed(
                            module.keepRotations.getValue()))
            {
                setRotations(module.rot, event);
            }
            else
            {
                module.rot = null;
            }
        }
        else
        {
            if (module.pos == null
                || module.facing == null
                || module.preRotate.getValue() != 0
                        && module.rotate.getValue()
                        && !module.rotationTimer.passed(
                                module.preRotate.getValue()))
            {
                return;
            }

            int slot = -1;
            int optional = -1;

            ItemStack offhand = mc.player.getHeldItemOffhand();
            if (module.isStackValid(offhand))
            {
                if (offhand.getItem() instanceof ItemBlock)
                {
                    Block block = ((ItemBlock) offhand.getItem()).getBlock();
                    if (!module.checkState.getValue()
                            || !block.getBlockState()
                                     .getBaseState()
                                     .getMaterial()
                                     .isReplaceable())
                    {
                        if (block instanceof BlockContainer)
                        {
                            optional = -2;
                        }
                        else
                        {
                            slot = -2;
                        }
                    }
                }
                else
                {
                    optional = -2;
                }
            }

            if (slot == -1)
            {
                for (int i = 0; i < 9; i++)
                {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (module.isStackValid(stack)
                            && stack.getItem() instanceof ItemBlock)
                    {
                        Block block = ((ItemBlock) stack.getItem()).getBlock();
                        if (!module.checkState.getValue()
                                || !block.getBlockState()
                                         .getBaseState()
                                         .getMaterial()
                                         .isReplaceable())
                        {
                            if (block instanceof BlockContainer)
                            {
                                optional = i;
                            }
                            else
                            {
                                slot = i;

                                if (i == mc.player.inventory.currentItem)
                                {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            slot = slot == -1 ? optional : slot;
            if (slot != -1)
            {
                boolean jump  = mc.player.movementInput.jump
                                    && module.tower.getValue();

                boolean sneak = mc.player.movementInput.sneak
                                    && module.down.getValue();

                if (jump && !sneak && !MovementUtil.isMoving())
                {
                    ((IMinecraft) mc).setRightClickDelay(3);
                    mc.player.jump();
                    if (module.towerTimer.passed(1500))
                    {
                        mc.player.motionY = -0.28;
                        module.towerTimer.reset();
                    }
                }
                else
                {
                    module.towerTimer.reset();
                }

                boolean sneaking = module.smartSneak.getValue() &&
                    !SpecialBlocks.shouldSneak(module.pos.offset(module.facing),
                                               true);
                if (module.attack.getValue()
                    && Managers.SWITCH.getLastSwitch()
                        > module.cooldown.getValue()
                    && module.breakTimer.passed(module.breakDelay.getValue()))
                {
                    Entity entity = null;
                    float minDmg = Float.MAX_VALUE;
                    for (EntityEnderCrystal crystal :
                            mc.world.getEntitiesWithinAABB(
                                    EntityEnderCrystal.class,
                                    new AxisAlignedBB(module.pos)))
                    {
                        if (crystal == null || crystal.isDead)
                        {
                            continue;
                        }

                        float damage = DamageUtil.calculate(crystal);
                        if (damage < minDmg
                                && module.pop
                                         .getValue()
                                         .shouldPop(damage,
                                                    module.popTime.getValue()))
                        {
                            entity = crystal;
                            minDmg = damage;
                        }
                    }

                    if (entity != null)
                    {
                        // TODO: AntiWeakness
                        PacketUtil.attack(entity);
                        module.breakTimer.reset();
                    }
                }

                int finalSlot = slot;
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                {
                    int lastSlot = mc.player.inventory.currentItem;
                    boolean sprinting = mc.player.isSprinting()
                            && module.stopSprint.getValue();
                    InventoryUtil.switchTo(finalSlot);

                    if (sprinting)
                    {
                        PacketUtil.sendAction(
                                CPacketEntityAction.Action.STOP_SPRINTING);
                    }

                    if (!sneaking)
                    {
                        PacketUtil.sendAction(
                                CPacketEntityAction.Action.START_SNEAKING);
                    }

                    RayTraceResult result =
                            RayTraceUtil.getRayTraceResult(module.rotations[0],
                                                           module.rotations[1]);

                    mc.playerController.processRightClickBlock(
                            mc.player,
                            mc.world,
                            module.pos.offset(module.facing),
                            module.facing.getOpposite(),
                            result.hitVec,
                            InventoryUtil.getHand(finalSlot));

                    mc.player.connection.sendPacket(
                        new CPacketAnimation(InventoryUtil.getHand(finalSlot)));

                    if (!sneaking)
                    {
                        PacketUtil.sendAction(
                                CPacketEntityAction.Action.STOP_SNEAKING);
                    }

                    if (sprinting)
                    {
                        PacketUtil.sendAction(
                                CPacketEntityAction.Action.START_SPRINTING);
                    }

                    InventoryUtil.switchTo(lastSlot);
                });

                if (module.swing.getValue())
                {
                    Swing.Client.swing(InventoryUtil.getHand(slot));
                }
            }
        }
    }

    private void setRotations(BlockPos pos, MotionUpdateEvent event)
    {
        if (module.raytrace.getValue())
        {
            if (rayTrace(pos, event))
            {
                return;
            }
        }
        else
        {
            module.facing = BlockUtil.getFacing(pos);
            if (module.facing != null)
            {
                setRotations(pos, event, module.facing);
                return;
            }
        }

        // Could find helping block?
        if (module.helping.getValue() && module.facing == null)
        {
            for (EnumFacing facing : EnumFacing.values())
            {
                BlockPos p = pos.offset(facing);
                if (module.raytrace.getValue())
                {
                    if (rayTrace(p, event))
                    {
                        module.pos = p;
                        return;
                    }
                }
                else
                {
                    EnumFacing f = BlockUtil.getFacing(p);
                    if (f != null)
                    {
                        module.facing = f;
                        module.pos = p;
                        setRotations(p, event, f);
                    }
                }
            }
        }
    }

    private boolean rayTrace(BlockPos pos, MotionUpdateEvent event) {
        Entity entity = RotationUtil.getRotationPlayer();
        Ray ray = RayTraceFactory.fullTrace(entity, HELPER, pos, -1.0);
        if (ray != null && ray.isLegit()) {
            module.facing = ray.getFacing().getOpposite();
            module.rotations = ray.getRotations();
            if (module.rotate.getValue() && module.rotations != null)
            {
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);
            }

            return true;
        }

        return false;
    }

    private void setRotations(BlockPos pos,
                              MotionUpdateEvent event,
                              EnumFacing facing)
    {
        module.rotations = RotationUtil.getRotations(pos.offset(facing),
                                                     facing.getOpposite());
        if (module.rotate.getValue() && module.rotations != null)
        {
            event.setYaw(module.rotations[0]);
            event.setPitch(module.rotations[1]);
        }
    }

}
