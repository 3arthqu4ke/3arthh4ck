package me.earth.earthhack.impl.modules.misc.autoregear;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

// TODO: move duplicated code to methods
final class ListenerMotion
        extends ModuleListener<AutoRegear, MotionUpdateEvent>
{
    public ListenerMotion(AutoRegear module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {

        }
        else
        {

        }

        if (module.steal.getValue()
                && mc.currentScreen == null
                && !module.shouldRegear)
        {
            BlockPos craftingPos = module.getShulkerBox();
            float[] rotations = RotationUtil.getRotations(craftingPos, EnumFacing.UP);
            RayTraceResult ray = RotationUtil.rayTraceTo(craftingPos, mc.world);
            float[] f = RayTraceUtil.hitVecToPlaceVec(craftingPos, ray.hitVec);
            if (module.rotate.getValue() == Rotate.Normal)
            {
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
            else if (module.rotate.getValue() != Rotate.None)
            {
                PacketUtil.doRotation(rotations[0], rotations[1], mc.player.onGround);
            }
            NetworkUtil.send(new CPacketPlayerTryUseItemOnBlock(craftingPos, ray.sideHit, EnumHand.MAIN_HAND, f[0], f[1], f[2]));
            return;
        }

        if (module.shouldRegear
                && mc.currentScreen == null)
        {
            BlockPos optimal = module.getOptimalPlacePos(false);
            int slot;
            boolean swapped = false;

            if (module.placeEchest.getValue()
                    && module.getBlock(Blocks.ENDER_CHEST) == null
                    && InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST) != -1
                    && !module.hasKit()
                    && optimal != null)
            {
                slot = InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST);
                if (slot == -1) return;
                module.slot = slot;
                EnumFacing facing = BlockUtil.getFacing(optimal);
                module.placeBlock(optimal.offset(facing), facing.getOpposite());
                if (module.rotate.getValue() == Rotate.Normal
                        && module.rotations != null)
                {
                    event.setYaw(module.rotations[0]);
                    event.setPitch(module.rotations[1]);
                }
                module.execute();
                return;
            }

            if (module.grabShulker.getValue()
                    && module.getBlock(Blocks.ENDER_CHEST) != null
                    && module.getShulkerBox() == null
                    && mc.currentScreen == null
                    && !module.hasKit())
            {
                BlockPos craftingPos = module.getBlock(Blocks.ENDER_CHEST);
                float[] rotations = RotationUtil.getRotations(craftingPos, EnumFacing.UP);
                RayTraceResult ray = RotationUtil.rayTraceTo(craftingPos, mc.world);
                float[] f = RayTraceUtil.hitVecToPlaceVec(craftingPos, ray.hitVec);
                if (module.rotate.getValue() == Rotate.Normal)
                {
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                }
                else if (module.rotate.getValue() != Rotate.None)
                {
                    PacketUtil.doRotation(rotations[0], rotations[1], mc.player.onGround);
                }
                NetworkUtil.send(new CPacketPlayerTryUseItemOnBlock(craftingPos, ray.sideHit, EnumHand.MAIN_HAND, f[0], f[1], f[2]));
                return;
            }

            if (module.placeShulker.getValue()
                    && module.getShulkerBox() == null
                    && module.hasKit()
                    && optimal != null)
            {
                optimal = module.getOptimalPlacePos(true);
                if (optimal == null) return;
                slot = InventoryUtil.findInHotbar(stack -> stack.getItem() instanceof ItemBlock
                        && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockShulkerBox);
                int inventorySlot = InventoryUtil.findInInventory(stack -> stack.getItem() instanceof ItemBlock
                        && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockShulkerBox, true);
                if (slot == -1) {
                    if (inventorySlot == -1) return;
                    slot = InventoryUtil.hotbarToInventory(8);
                    mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
                    swapped = true;
                }
                module.slot = slot;
                EnumFacing facing = EnumFacing.DOWN;
                module.placeBlock(optimal.offset(facing), facing.getOpposite());
                if (module.rotate.getValue() == Rotate.Normal
                        && module.rotations != null)
                {
                    event.setYaw(module.rotations[0]);
                    event.setPitch(module.rotations[1]);
                }
                module.execute();
                if (swapped)
                {
                    mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
                }
                return;
            }

            BlockPos craftingPos = module.getShulkerBox();
            if (craftingPos == null) return;
            float[] rotations = RotationUtil.getRotations(craftingPos, EnumFacing.UP);
            RayTraceResult ray = RotationUtil.rayTraceTo(craftingPos, mc.world);
            float[] f = RayTraceUtil.hitVecToPlaceVec(craftingPos, ray.hitVec);
            if (module.rotate.getValue() == Rotate.Normal)
            {
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
            else if (module.rotate.getValue() != Rotate.None)
            {
                PacketUtil.doRotation(rotations[0], rotations[1], mc.player.onGround);
            }
            NetworkUtil.send(new CPacketPlayerTryUseItemOnBlock(craftingPos, ray.sideHit, EnumHand.MAIN_HAND, f[0], f[1], f[2]));
            module.shouldRegear = false;
        }
    }
}
