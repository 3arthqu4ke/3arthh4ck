package me.earth.earthhack.impl.modules.movement.nofall;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.nofall.mode.FallMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion extends ModuleListener<NoFall, MotionUpdateEvent>
{
    public ListenerMotion(NoFall module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (module.mode.getValue() == FallMode.Bucket)
        {
            int slot = InventoryUtil.findHotbarItem(Items.WATER_BUCKET);
            if (slot != -1)
            {
                Vec3d positionVector = mc.player.getPositionVector();
                RayTraceResult rayTraceBlocks =
                        mc.world.rayTraceBlocks(
                                positionVector,
                                new Vec3d(positionVector.x,
                                          positionVector.y - 3.0,
                                          positionVector.z),
                                true);

                if (mc.player.fallDistance < 5.0f
                        || rayTraceBlocks == null
                        || rayTraceBlocks.typeOfHit != RayTraceResult.Type.BLOCK
                        || mc.world.getBlockState(
                                rayTraceBlocks.getBlockPos()).getBlock()
                                                        instanceof BlockLiquid
                        || PositionUtil.inLiquid()
                        || PositionUtil.inLiquid(false))
                {
                    return;
                }

                if (event.getStage() == Stage.PRE)
                {
                    event.setPitch(90.0f);
                }
                else
                {
                    RayTraceResult rayTraceBlocks2 =
                            mc.world.rayTraceBlocks(
                                    positionVector,
                                    new Vec3d(positionVector.x,
                                              positionVector.y - 5.0,
                                              positionVector.z),
                                    true);

                    if (rayTraceBlocks2 != null
                            && rayTraceBlocks2.typeOfHit ==
                                                    RayTraceResult.Type.BLOCK
                            && !(mc.world.getBlockState(
                                    rayTraceBlocks2.getBlockPos()).getBlock()
                                                        instanceof BlockLiquid)
                            && module.timer.passed(1000))
                    {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                        {
                            InventoryUtil.switchTo(slot);
                            mc.playerController
                              .processRightClick(mc.player,
                                                 mc.world,
                                                 slot == -2
                                                    ? EnumHand.OFF_HAND
                                                    : EnumHand.MAIN_HAND);
                        });

                        module.timer.reset();
                    }
                }
            }
        }
    }

}
