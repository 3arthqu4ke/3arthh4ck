package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
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
        if (mc.player.isDead
                || mc.player.isSneaking()
                || !module.timer.passed(800))
        {
            return;
        }

        switch (module.mode.getValue())
        {
            case Dolphin:
                if (PositionUtil.inLiquid()
                        && mc.player.fallDistance < 3.0f
                        && !mc.player.isSneaking())
                {
                    mc.player.motionY = 0.1;
                }

                return;
            case Trampoline:
                if (event.getStage() == Stage.PRE)
                {
                    if (PositionUtil.inLiquid(false) && !mc.player.isSneaking())
                    {
                        mc.player.onGround = false;
                    }

                    Block block =
                        mc.world.getBlockState(new BlockPos(mc.player.posX,
                                                            mc.player.posY,
                                                            mc.player.posZ))
                                                .getBlock();

                    if (module.jumped
                            && !mc.player.capabilities.isFlying
                            && !mc.player.isInWater())
                    {
                        if (mc.player.motionY < -0.3
                                || mc.player.onGround
                                || mc.player.isOnLadder())
                        {
                            module.jumped = false;
                            return;
                        }

                        mc.player.motionY =
                                mc.player.motionY / 0.9800000190734863 + 0.08;
                        mc.player.motionY -= 0.03120000000005;
                    }

                    if (mc.player.isInWater() || mc.player.isInLava())
                    {
                        mc.player.motionY = 0.1;
                        break;
                    }

                    if (!mc.player.isInLava()
                            && block instanceof BlockLiquid
                            && mc.player.motionY < 0.2)
                    {
                        mc.player.motionY = 0.5;
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
                && mc.player.ticksExisted % 2 == 0)
        {
            event.setY(event.getY() + 0.02);
        }
    }

}
