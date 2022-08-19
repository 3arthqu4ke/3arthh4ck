package me.earth.earthhack.impl.modules.combat.selftrap;

import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyUtil;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

final class ListenerSelfTrap extends ObbyListener<SelfTrap>
{
    public ListenerSelfTrap(SelfTrap module)
    {
        super(module, -9);
    }

    @Override
    protected boolean updatePlaced()
    {
        if (!module.autoOff.getValue())
        {
            return super.updatePlaced();
        }

        BlockPos p = PositionUtil.getPosition(RotationUtil.getRotationPlayer());
        if (!p.equals(module.startPos))
        {
            module.disable();
            return true;
        }
        else if (module.smartOff.getValue())
        {
            for (Vec3i offset : module.mode.getValue().getOffsets())
            {
                if (ObbyModule.HELPER
                              .getBlockState(p.add(offset))
                              .getBlock() != module.mode.getValue().getBlock())
                {
                    return super.updatePlaced();
                }
            }

            module.disable();
            return true;
        }

        return super.updatePlaced();
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        if (module.smart.getValue())
        {
            EntityPlayer closest = EntityUtil.getClosestEnemy();
            if (closest == null
                || mc.player.getDistanceSq(closest) >
                    MathUtil.square(module.range.getValue()))
            {
                return result.setValid(false);
            }
        }

        if (module.mode.getValue() != SelfTrapMode.Obsidian)
        {
            for (Vec3i offset : module.mode.getValue().getOffsets())
            {
                result.getTargets().add(
                    PositionUtil.getPosition(RotationUtil.getRotationPlayer())
                                .add(offset));
            }

            return result;
        }

        BlockPos pos = PositionUtil.getPosition(RotationUtil.getRotationPlayer())
                                   .up(2);
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable())
        {
            return result.setValid(false);
        }

        for (BlockPos alreadyPlaced : placed.keySet())
        {
            ObbyModule.HELPER.addBlockState(alreadyPlaced,
                                            Blocks.OBSIDIAN.getDefaultState());
        }

        BasePath path = new BasePath(RotationUtil.getRotationPlayer(),
                                     pos,
                                     module.maxHelping.getValue());

        if (module.prioBehind.getValue())
        {
            List<BlockPos> checkFirst = new ArrayList<>(13);
            EnumFacing look = mc.player.getHorizontalFacing();
            BlockPos off = pos.offset(look.getOpposite());

            checkFirst.add(off);
            checkFirst.add(off.down());
            checkFirst.add(off.down(2));
            checkFirst.add(pos.up());

            for (EnumFacing facing : EnumFacing.values())
            {
                if (facing == look)
                {
                    continue;
                }

                checkFirst.add(off.offset(facing));
                checkFirst.add(off.down().offset(facing));
                checkFirst.add(off.down(2).offset(facing));
            }

            PathFinder.efficient(
                path,
                module.placeRange.getValue(),
                mc.world.loadedEntityList,
                module.smartRay.getValue(),
                ObbyModule.HELPER,
                Blocks.OBSIDIAN.getDefaultState(),
                PathFinder.CHECK,
                checkFirst,
                pos.down(),
                pos.down(2));
        }
        else
        {
            PathFinder.findPath(
                path,
                module.placeRange.getValue(),
                mc.world.loadedEntityList,
                module.smartRay.getValue(),
                ObbyModule.HELPER,
                Blocks.OBSIDIAN.getDefaultState(),
                PathFinder.CHECK,
                pos.down(),
                pos.down(2));
        }

        return result.setValid(ObbyUtil.place(module, path));
    }

    @Override
    protected int getSlot()
    {
        switch (module.mode.getValue())
        {
            case Obsidian:
                return InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
            case Web:
            case HighWeb:
            case FullWeb:
                return InventoryUtil.findHotbarBlock(Blocks.WEB);
            default:
                return -1;
        }
    }

    @Override
    protected String getDisableString()
    {
        switch (module.mode.getValue())
        {
            case Obsidian:
                return "Disabled, no Obsidian.";
            case Web:
            case HighWeb:
            case FullWeb:
                return "Disabled, no Webs.";
            default:
                return "Disabled, unknown Mode!";
        }
    }

}
