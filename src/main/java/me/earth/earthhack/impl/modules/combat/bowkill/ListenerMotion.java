package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion extends ModuleListener<BowKiller, MotionUpdateEvent> {
    public ListenerMotion(BowKiller module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        module.entityDataArrayList.removeIf(e -> e.getTime() + 60000 < System.currentTimeMillis());
        if (!mc.player.collidedVertically)
            return;


        if (event.getStage() == Stage.PRE) {
            module.blockUnder = isBlockUnder();
            if (module.rotate.getValue() && mc.player.getActiveItemStack().getItem() == Items.BOW
                    && mc.gameSettings.keyBindUseItem.isKeyDown() && module.blockUnder) {
                module.target = module.findTarget();
                if (module.target != null) {
                    float[] rotations
                            = module.rotationSmoother
                            .getRotations(RotationUtil.getRotationPlayer(),
                                    module.target,
                                    module.height.getValue(),
                                    module.soft.getValue());
                    if (rotations != null) {
                        if (module.silent.getValue()) {
                            event.setYaw(rotations[0]);
                            event.setPitch(rotations[1]);
                        } else {
                            mc.player.rotationYaw = rotations[0];
                            mc.player.rotationPitch = rotations[1];
                        }
                    }
                }
            }
            if (mc.player.getActiveItemStack().getItem() == Items.BOW
                    && mc.player.isHandActive()) {
                if (!module.blockUnder) {
                    final int newSlot = findBlockInHotbar();
                    if (newSlot != -1) {
                        final int oldSlot = mc.player.inventory.currentItem;
                        mc.player.inventory.currentItem = newSlot;
                        placeBlock(PositionUtil.getPosition(RotationUtil.getRotationPlayer()).down(1), event);
                        mc.player.inventory.currentItem = oldSlot;
                    }
                }
            }
        } else {
            if (mc.player.getActiveItemStack().getItem() != Items.BOW) {
                module.cancelling = false;
                module.packetsSent = 0;
            } else if (mc.player.getActiveItemStack().getItem() == Items.BOW
                    && mc.player.isHandActive()
                    && module.cancelling && module.blockUnder) {

                module.packetsSent++; // The server expects a packet to be sent every time this event is called.
                if (module.packetsSent > module.runs.getValue() * 2
                        && !module.always.getValue()
                        && module.needsMessage) {
                    ModuleUtil.sendMessage(module, TextColor.GREEN + "Charged!");
                }
            }
        }
    }

    private int findBlockInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockObsidian) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean canBeClicked(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
    }

    private void placeBlock(BlockPos pos, MotionUpdateEvent event) {
        for (EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (!canBeClicked(neighbor))
                continue;
            final Vec3d hitVec = new Vec3d(neighbor).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
            float[] rotations
                    = RotationUtil.getRotations(hitVec);
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            return;
        }
    }

    private boolean isBlockUnder() {
        return !(mc.world.getBlockState(PositionUtil.getPosition(RotationUtil.getRotationPlayer()).down(1)).getBlock() instanceof BlockAir);
    }
}
