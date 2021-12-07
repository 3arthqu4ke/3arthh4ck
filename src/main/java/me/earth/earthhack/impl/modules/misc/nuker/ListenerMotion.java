package me.earth.earthhack.impl.modules.misc.nuker;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemFood;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

final class ListenerMotion extends ModuleListener<Nuker, MotionUpdateEvent>
{
    public ListenerMotion(Nuker module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            if (module.nuke.getValue() && module.currentSelection != null)
            {
                module.currentSelection.removeIf(pos ->
                        !MineUtil.canBreak(pos)
                                || BlockUtil.getDistanceSqDigging(pos)
                                    > MathUtil.square(module.range.getValue()));

                if (module.timer.passed(module.timeout.getValue())
                        && module.actions.isEmpty())
                {
                    module.breakSelection(module.currentSelection,
                                          module.autoTool.getValue());
                }
            }

            Set<Block> blocks = module.getBlocks();
            if (!blocks.isEmpty()
                    && module.timer.passed(module.timeout.getValue())
                    && module.actions.isEmpty())
            {
                if (mc.player.getActiveItemStack().getItem()
                            instanceof ItemFood)
                {
                    return;
                }

                Set<BlockPos> toAttack = new HashSet<>();
                BlockPos middle = PositionUtil.getPosition();
                int maxRadius = Sphere.getRadius(module.range.getValue());
                for (int i = 1; i < maxRadius; i++)
                {
                    BlockPos pos = middle.add(Sphere.get(i));
                    if (BlockUtil.getDistanceSq(pos)
                            > MathUtil.square(module.range.getValue()))
                    {
                        continue;
                    }

                    if (blocks.contains(mc.world.getBlockState(pos).getBlock()))
                    {
                        toAttack.add(pos);
                        if (module.rotate.getValue() == Rotate.Normal)
                        {
                            break;
                        }
                    }
                }

                module.breakSelection(toAttack, true);
            }

            if (module.rotations != null)
            {
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);
            }

            module.rotations = null;
        }
        else
        {
            module.lastSlot = -1;
            Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                          () -> CollectionUtil.emptyQueue(module.actions));
            module.breaking = false;
        }
    }

}
