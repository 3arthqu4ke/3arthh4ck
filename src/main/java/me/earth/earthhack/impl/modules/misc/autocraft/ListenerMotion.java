package me.earth.earthhack.impl.modules.misc.autocraft;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

final class ListenerMotion
        extends ModuleListener<AutoCraft, MotionUpdateEvent>
{
    public ListenerMotion(AutoCraft module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        BlockPos pos = module.getCraftingTableBlock();
        BlockPos wackyPos = module.getCraftingTable();
        int inventorySlot = InventoryUtil.findBlock(Blocks.CRAFTING_TABLE, false);
        int slot = InventoryUtil.findHotbarBlock(Blocks.CRAFTING_TABLE);
        boolean swapped = false;
        if (slot == -1
                && inventorySlot != -1)
        {
            mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, InventoryUtil.hotbarToInventory(8), 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
            swapped = true;
        }
        slot = InventoryUtil.findHotbarBlock(Blocks.CRAFTING_TABLE);
        if (module.shouldTable
                && (pos != null || wackyPos != null)
                && slot != -1)
        {
            if (wackyPos == null
                    && module.placeTable.getValue())
            {
                module.slot = slot;
                EnumFacing facing = BlockUtil.getFacing(pos);
                module.placeBlock(pos.offset(facing), facing.getOpposite());
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
                    mc.playerController.windowClick(0, InventoryUtil.hotbarToInventory(8), 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
                }
            }
            else if (wackyPos != null
                    && mc.currentScreen == null)
            {
                BlockPos craftingPos = module.getCraftingTable();
                float[] rotations = RotationUtil.getRotations(craftingPos, EnumFacing.UP);
                RayTraceResult ray = RotationUtil.rayTraceTo(craftingPos, mc.world);
                float[] f = RayTraceUtil.hitVecToPlaceVec(craftingPos, ray.hitVec);
                if (module.rotate.getValue() == Rotate.Normal)
                {
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                }
                else if (module.rotate.getValue() == Rotate.None)
                {
                    PacketUtil.doRotation(rotations[0], rotations[1], mc.player.onGround);
                }
                NetworkUtil.send(new CPacketPlayerTryUseItemOnBlock(craftingPos, ray.sideHit, EnumHand.MAIN_HAND, f[0], f[1], f[2]));
                module.shouldTable = false;
            }
        }
    }
}
