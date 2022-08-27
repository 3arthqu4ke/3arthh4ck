package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PlaceData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class HelperLiquids implements Globals
{
    private final AutoCrystal module;

    public HelperLiquids(AutoCrystal module) {
        this.module = module;
    }

    public PlaceData calculate(HelperPlace placeHelper,
                               PlaceData placeData,
                               List<EntityPlayer> friends,
                               List<EntityPlayer> players,
                               float minDamage)
    {
        PlaceData newData = new PlaceData(minDamage);
        newData.setTarget(placeData.getTarget());
        for (PositionData data : placeData.getLiquid())
        {
            if (placeHelper.validate(placeData, data, friends) != null)
            {
                placeHelper.calcPositionData(newData, data, players);
            }
        }

        return newData;
    }

    public EnumFacing getAbsorbFacing(BlockPos pos,
                                      List<Entity> entities,
                                      IBlockAccess access,
                                      double placeRange)
    {
        for (EnumFacing facing : EnumFacing.values())
        {
            if (facing == EnumFacing.DOWN)
            {
                continue;
            }

            BlockPos offset = pos.offset(facing);
            if (BlockUtil.getDistanceSq(offset) >= MathUtil.square(placeRange))
            {
                continue;
            }

            if (access.getBlockState(offset).getMaterial().isReplaceable())
            {
                boolean found = false;
                AxisAlignedBB bb = new AxisAlignedBB(offset);
                for (Entity entity : entities)
                {
                    if (entity == null
                        || EntityUtil.isDead(entity)
                        || !entity.preventEntitySpawning)
                    {
                        continue;
                    }

                    if (module.bbBlockingHelper.blocksBlock(bb, entity))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    return facing;
                }
            }
        }

        return null;
    }

    // TODO: make this utility method somewhere else, MineUtil maybe
    public static MineSlots getSlots(boolean onGroundCheck)
    {
        int bestBlock = -1;
        int bestTool  = -1;
        float maxSpeed = 0.0f;
        for (int i = 8; i > -1; i--)
        {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBlock)
            {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                int tool = MineUtil.findBestTool(BlockPos.ORIGIN,
                                                 block.getDefaultState());
                float damage = MineUtil.getDamage(
                        block.getDefaultState(),
                        mc.player.inventory.getStackInSlot(tool),
                        BlockPos.ORIGIN,
                        !onGroundCheck
                            || RotationUtil.getRotationPlayer().onGround);

                if (damage > maxSpeed)
                {
                    bestBlock = i;
                    bestTool  = tool;
                    maxSpeed  = damage;
                }
            }
        }

        return new MineSlots(bestBlock, bestTool, maxSpeed);
    }

}
