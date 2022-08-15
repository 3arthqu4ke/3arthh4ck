package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Phase;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerMotion extends ModuleListener<PacketFly, MotionUpdateEvent>
{
    public ListenerMotion(PacketFly module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE
            && module.mode.getValue() != Mode.Compatibility)
        {
            mc.player.motionX = 0.0;
            mc.player.motionY = 0.0;
            mc.player.motionZ = 0.0;

            if (module.mode.getValue() != Mode.Setback
                    && module.teleportID.get() == 0)
            {
                if (module.checkPackets(6))
                {
                    module.sendPackets(0.0, 0.0, 0.0, true);
                }

                return;
            }

            boolean isPhasing = module.isPlayerCollisionBoundingBoxEmpty();
            double ySpeed;

            if (mc.player.movementInput.jump
                    && (isPhasing || !MovementUtil.isMoving()))
            {
                if (module.antiKick.getValue() && !isPhasing)
                {
                    ySpeed = module.checkPackets(
                                    module.mode.getValue() == Mode.Setback
                                            ? 10
                                            : 20)
                                ? -0.032
                                : 0.062;
                }
                else
                {
                    ySpeed = module.yJitter.getValue() && module.zoomies
                            ? 0.061
                            : 0.062;
                }
            }
            else if (mc.player.movementInput.sneak)
            {
                ySpeed = module.yJitter.getValue() && module.zoomies
                        ? -0.061
                        : -0.062;
            }
            else
            {
                ySpeed = !isPhasing
                            ? (module.checkPackets(4)
                                ? (module.antiKick.getValue()
                                    ? -0.04
                                    : 0.0)
                                : 0.0)
                            : 0.0;
            }

            if (module.phase.getValue() == Phase.Full
                    && isPhasing
                    && MovementUtil.isMoving()
                    && ySpeed != 0.0)
            {
                ySpeed /= 2.5;
            }

            double high = module.xzJitter.getValue() && module.zoomies
                    ? 0.25
                    : 0.26;
            double low = module.xzJitter.getValue() && module.zoomies
                    ? 0.030
                    : 0.031;

            double[] dirSpeed = MovementUtil.strafe(
                        module.phase.getValue() == Phase.Full
                                && isPhasing
                                ? low
                                : high);

            if (module.mode.getValue() == Mode.Increment)
            {
                if (module.lastFactor >= module.factor.getValue())
                {
                    module.lastFactor = 1.0f;
                }
                else if (++module.lastFactor > module.factor.getValue())
                {
                    module.lastFactor = module.factor.getValue();
                }
            }
            else
            {
                module.lastFactor = module.factor.getValue();
            }

            for (int i = 1; i <= (module.mode.getValue() == Mode.Factor
                                    || module.mode.getValue() == Mode.Slow
                                    || module.mode.getValue() == Mode.Increment
                                    ? module.lastFactor
                                    : 1); i++)
            {
                double conceal = mc.player.posY < module.concealY.getValue()
                        && !MovementUtil.noMovementKeys()
                            ? module.conceal.getValue()
                            : 1.0;

                mc.player.motionX = dirSpeed[0] * i * conceal * module.xzSpeed.getValue();
                mc.player.motionY = ySpeed      * i * module.ySpeed.getValue();
                mc.player.motionZ = dirSpeed[1] * i * conceal * module.xzSpeed.getValue();
                module.sendPackets(
                        mc.player.motionX,
                        mc.player.motionY,
                        mc.player.motionZ,
                        module.mode.getValue() != Mode.Setback);
            }

            module.zoomTimer++;
            if (module.zoomTimer > module.zoomer.getValue())
            {
                module.zoomies = !module.zoomies;
                module.zoomTimer = 0;
            }
        }
    }

}
