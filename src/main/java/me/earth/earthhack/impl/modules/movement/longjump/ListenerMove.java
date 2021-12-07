package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.longjump.mode.JumpMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerMove extends ModuleListener<LongJump, MoveEvent>
{
    public ListenerMove(LongJump module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (module.mode.getValue() == JumpMode.Normal)
        {
            if (mc.player.moveStrafing <= 0.0f && mc.player.moveForward <= 0.0f)
            {
                module.stage = 1;
            }

            if (MathUtil.round(mc.player.posY - (int)mc.player.posY, 3)
                    == MathUtil.round(0.943, 3))
            {
                mc.player.motionY -= 0.03;
                event.setY(event.getY() - 0.03);
            }

            if (module.stage == 1 && MovementUtil.isMoving())
            {
                module.stage = 2;
                module.speed = module.boost.getValue()
                        * MovementUtil.getSpeed() - 0.01;
            }
            else if (module.stage == 2)
            {
                module.stage = 3;
                mc.player.motionY = 0.424;
                event.setY(0.424);
                module.speed = module.speed * 2.149802;
            }
            else if (module.stage == 3)
            {
                module.stage = 4;
                double difference = 0.66D
                        * (module.distance - MovementUtil.getSpeed());
                module.speed = (module.distance - difference);
            }
            else
            {
                if (mc.world.getCollisionBoxes(mc.player,
                        mc.player
                            .getEntityBoundingBox()
                            .offset(0.0, mc.player.motionY, 0.0)).size() > 0
                        || mc.player.collidedVertically)
                {
                    module.stage = 1;
                }

                module.speed = module.distance - module.distance / 159.0;
            }

            module.speed = Math.max(module.speed, MovementUtil.getSpeed());
            MovementUtil.strafe(event, module.speed);

            float moveForward = mc.player.movementInput.moveForward;
            float moveStrafe = mc.player.movementInput.moveStrafe;
            float rotationYaw = mc.player.rotationYaw;
            if (moveForward == 0.0f && moveStrafe == 0.0f)
            {
                event.setX(0.0);
                event.setZ(0.0);
            }
            else
            {
                if (moveForward != 0.0f)
                {
                    if (moveStrafe >= 1.0f)
                    {
                        rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
                        moveStrafe = 0.0f;
                    }
                    else
                    {
                        if (moveStrafe <= -1.0f)
                        {
                            rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
                            moveStrafe = 0.0f;
                        }
                    }

                    if (moveForward > 0.0f)
                    {
                        moveForward = 1.0f;
                    }
                    else if (moveForward < 0.0f)
                    {
                        moveForward = -1.0f;
                    }
                }
            }

            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));

            event.setX(moveForward
                            * module.speed
                            * cos
                        + moveStrafe
                            * module.speed
                            * sin);

            event.setZ(moveForward
                            * module.speed
                            * sin
                        - moveStrafe
                            * module.speed
                            * cos);
        }
    }
}
