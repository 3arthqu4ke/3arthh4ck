package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class MovementUtil implements Globals
{
    public static boolean isMoving()
    {
        return mc.player.moveForward != 0.0 || mc.player.moveStrafing != 0.0;
    }

    public static boolean anyMovementKeys()
    {
        return mc.player.movementInput.forwardKeyDown
                || mc.player.movementInput.backKeyDown
                || mc.player.movementInput.leftKeyDown
                || mc.player.movementInput.rightKeyDown
                || mc.player.movementInput.jump
                || mc.player.movementInput.sneak;
    }

    public static boolean noMovementKeys()
    {
        return !mc.player.movementInput.forwardKeyDown
                && !mc.player.movementInput.backKeyDown
                && !mc.player.movementInput.rightKeyDown
                && !mc.player.movementInput.leftKeyDown;
    }

    public static boolean noMovementKeysOrJump()
    {
        return noMovementKeys()
                && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
    }

    public static void setMoveSpeed(double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            mc.player.motionX = forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw));
            mc.player.motionZ = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw));
        }
    }


    public static void strafe(MoveEvent event, double speed)
    {
        if (isMoving())
        {
            double[] strafe = strafe(speed);
            event.setX(strafe[0]);
            event.setZ(strafe[1]);
        }
        else
        {
            event.setX(0.0);
            event.setZ(0.0);
        }
    }

    public static double[] strafe(double speed)
    {
        return strafe(mc.player, speed);
    }

    public static double[] strafe(Entity entity, double speed)
    {
        return strafe(entity, mc.player.movementInput, speed);
    }

    public static double[] strafe(Entity entity,
                                  MovementInput movementInput,
                                  double speed)
    {
        float moveForward = movementInput.moveForward;
        float moveStrafe  = movementInput.moveStrafe;
        float rotationYaw = entity.prevRotationYaw
                                + (entity.rotationYaw - entity.prevRotationYaw)
                                * mc.getRenderPartialTicks();

        if (moveForward != 0.0f)
        {
            if (moveStrafe > 0.0f)
            {
                rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
            }
            else if (moveStrafe < 0.0f)
            {
                rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f)
            {
                moveForward = 1.0f;
            }
            else if (moveForward < 0.0f)
            {
                moveForward = -1.0f;
            }
        }

        double posX =
                moveForward * speed * -Math.sin(Math.toRadians(rotationYaw))
                + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ =
                moveForward * speed * Math.cos(Math.toRadians(rotationYaw))
                - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));

        return new double[] {posX, posZ};
    }

    public static MovementInput inverse(Entity entity, double speed)
    {
        MovementInput input = new MovementInput();
        input.sneak = entity.isSneaking();

        for (float d = -1.0f; d <= 1.0f; d += 1.0f)
        {
            for (float e = -1.0f; e <= 1.0f; e += 1.0f)
            {
                MovementInput dummyInput = new MovementInput();
                dummyInput.moveForward = d;
                dummyInput.moveStrafe = e;
                dummyInput.sneak = entity.isSneaking();
                double[] moveVec = strafe(entity, dummyInput, speed);
                if (entity.isSneaking())
                {
                    moveVec[0] *= 0.3f;
                    moveVec[1] *= 0.3f;
                }

                double targetMotionX = moveVec[0];
                double targetMotionZ = moveVec[1];
                if ((targetMotionX < 0 ? entity.motionX <= targetMotionX : entity.motionX >= targetMotionX)
                    && (targetMotionZ < 0 ? entity.motionZ <= targetMotionZ : entity.motionZ >= targetMotionZ))
                {
                    input.moveForward = d;
                    input.moveStrafe = e;
                    break;
                }
            }
        }

        return input;
    }

    public static double getDistance2D()
    {
        double xDist = mc.player.posX - mc.player.prevPosX;
        double zDist = mc.player.posZ - mc.player.prevPosZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static double getDistance3D()
    {
        double xDist = mc.player.posX - mc.player.prevPosX;
        double yDist = mc.player.posY - mc.player.prevPosY;
        double zDist = mc.player.posZ - mc.player.prevPosZ;
        return Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }

    // TODO: Slowness?
    public static double getSpeed()
    {
        return getSpeed(false);
    }

    public static double getSpeed(boolean slowness, double defaultSpeed)
    {

        if (mc.player.isPotionActive(MobEffects.SPEED))
        {
            int amplifier = Objects.requireNonNull(
                    mc.player.getActivePotionEffect(MobEffects.SPEED))
                    .getAmplifier();

            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        if (slowness && mc.player.isPotionActive(MobEffects.SLOWNESS))
        {
            int amplifier = Objects.requireNonNull(
                    mc.player.getActivePotionEffect(MobEffects.SLOWNESS))
                    .getAmplifier();

            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }

        return defaultSpeed;
    }

    public static double getSpeed(boolean slowness)
    {
        double defaultSpeed = 0.2873;

        if (mc.player.isPotionActive(MobEffects.SPEED))
        {
            int amplifier = Objects.requireNonNull(
                    mc.player.getActivePotionEffect(MobEffects.SPEED))
                    .getAmplifier();

            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        if (slowness && mc.player.isPotionActive(MobEffects.SLOWNESS))
        {
            int amplifier = Objects.requireNonNull(
                    mc.player.getActivePotionEffect(MobEffects.SLOWNESS))
                             .getAmplifier();

            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }

        return defaultSpeed;
    }

    public static double getJumpSpeed()
    {
        double defaultSpeed = 0.0;

        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
        {
            //noinspection ConstantConditions
            int amplifier = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
            defaultSpeed += (amplifier + 1) * 0.1;
        }

        return defaultSpeed;
    }

    public static boolean isInMovementDirection(double x, double y, double z)
    {
        if (mc.player.motionX != 0.0 || mc.player.motionZ != 0.0)
        {
            BlockPos movingPos = new BlockPos(mc.player)
                    .add(mc.player.motionX * 10000, 0, mc.player.motionZ * 10000);

            BlockPos antiPos   = new BlockPos(mc.player)
                    .add(mc.player.motionX * -10000, 0, mc.player.motionY * -10000);

            return movingPos.distanceSq(x, y, z) < antiPos.distanceSq(x, y, z);
        }

        return true;
    }

}
