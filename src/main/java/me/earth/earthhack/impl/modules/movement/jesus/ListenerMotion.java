package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion extends ModuleListener<Jesus, MotionUpdateEvent>
{
    public ListenerMotion(Jesus module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        Entity entity = PositionUtil.getPositionEntity();
        if (entity == null
            || entity.isDead
            || entity.isSneaking()
            || !module.timer.passed(800))
        {
            return;
        }

        switch (module.mode.getValue())
        {
            case Dolphin:
                if (PositionUtil.inLiquid()
                        && entity.fallDistance < 3.0f
                        && !entity.isSneaking())
                {
                    entity.motionY = 0.1;
                }

                return;
            case Trampoline:
                if (event.getStage() == Stage.PRE)
                {
                    if (PositionUtil.inLiquid(false) && !entity.isSneaking())
                    {
                        entity.onGround = false;
                    }

                    Block block =
                        mc.world.getBlockState(new BlockPos(entity.posX,
                                                            entity.posY,
                                                            entity.posZ))
                                                .getBlock();

                    if (module.jumped
                            && !mc.player.capabilities.isFlying
                            && !entity.isInWater())
                    {
                        if (entity.motionY < -0.3
                                || entity.onGround
                                || mc.player.isOnLadder())
                        {
                            module.jumped = false;
                            return;
                        }

                        entity.motionY =
                                entity.motionY / 0.9800000190734863 + 0.08;
                        entity.motionY -= 0.03120000000005;
                    }

                    if (entity.isInWater() || entity.isInLava())
                    {
                        entity.motionY = 0.1;
                        break;
                    }

                    if (!entity.isInLava()
                            && block instanceof BlockLiquid
                            && entity.motionY < 0.2)
                    {
                        entity.motionY = 0.5;
                        module.jumped = true;
                    }
                }

                break;
            default:
        }

        if (event.getStage() == Stage.PRE
                && !PositionUtil.inLiquid()
                && PositionUtil.inLiquid(true)
                && !PositionUtil.isMovementBlocked()
                && entity.ticksExisted % 2 == 0)
        {
            event.setY(event.getY() + 0.02);
        }
    }

}
