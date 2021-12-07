package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

final class ListenerObby extends ObbyListener<HighJump>
{
    public ListenerObby(HighJump module, int priority)
    {
        super(module, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (!module.scaffold.getValue()
            || !mc.gameSettings.keyBindJump.isKeyDown()
            || module.motionY < module.scaffoldY.getValue()
            || module.motionY > module.scaffoldMaxY.getValue()
            || InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN) == -1)
        {
            return;
        }

        super.invoke(event);
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        BlockPos pos =
            PositionUtil.getPosition(RotationUtil.getRotationPlayer());

        BlockPos firstSolid = null;
        for (int y = module.scaffoldOffset.getValue();
             y <= module.range.getValue();
             y++)
        {
            BlockPos p = pos.down(y);
            IBlockState state = mc.world.getBlockState(p);
            if (state.getMaterial().blocksMovement()
                    && !state.getMaterial().isReplaceable())
            {
                firstSolid = p;
                break;
            }
        }

        if (firstSolid == null)
        {
            return result;
        }

        for (int y = firstSolid.getY();
             y >= module.scaffoldOffset.getValue();
             y--)
        {
            BlockPos p = pos.down(y);
            if (p.equals(firstSolid))
            {
                continue;
            }

            result.getTargets().add(p);
        }

        return result;
    }

    @Override
    protected void disableModule() { }

}
