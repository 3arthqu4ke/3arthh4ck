package me.earth.earthhack.impl.util.math.rotation;

import me.earth.earthhack.impl.managers.minecraft.movement.RotationManager;
import net.minecraft.entity.Entity;

// TODO: distinguish between yaw- and pitch speed?
public class RotationSmoother
{
    private final RotationManager manager;
    private int rotationTicks;
    private boolean rotating;

    public RotationSmoother(RotationManager manager)
    {
        this.manager = manager;
    }

    public float[] getRotations(Entity from,
                                Entity entity,
                                double height,
                                double maxAngle)
    {
        return getRotations(entity,
                            from.posX,
                            from.posY,
                            from.posZ,
                            from.getEyeHeight(),
                            height,
                            maxAngle);
    }

    public float[] getRotations(Entity entity,
                                double fromX,
                                double fromY,
                                double fromZ,
                                float eyeHeight,
                                double height,
                                double maxAngle)
    {
        float[] rotations = RotationUtil.getRotations(
                        entity.posX,
                        entity.posY + entity.getEyeHeight() * height,
                        entity.posZ,
                        fromX,
                        fromY,
                        fromZ,
                        eyeHeight);

        return smoothen(rotations, maxAngle);
    }

    public float[] smoothen(float[] rotations,
                            double maxAngle)
    {
        float[] server = { manager.getServerYaw(), manager.getServerPitch() };
        return smoothen(server, rotations, maxAngle);
    }

    public float[] smoothen(float[] server,
                            float[] rotations,
                            double maxAngle)
    {
        if (maxAngle >= 180.0f
                || maxAngle <= 0.0f
                || RotationUtil.angle(server, rotations) <= maxAngle)
        {
            rotating = false;
            return rotations;
        }

        rotationTicks = 0;
        rotating = true;
        return RotationUtil.faceSmoothly(server[0],
                                         server[1],
                                         rotations[0],
                                         rotations[1],
                                         maxAngle,
                                         maxAngle);
    }

    public void incrementRotationTicks()
    {
        rotationTicks++;
    }

    public int getRotationTicks()
    {
        return rotationTicks;
    }

    public boolean isRotating()
    {
        return rotating;
    }

}
