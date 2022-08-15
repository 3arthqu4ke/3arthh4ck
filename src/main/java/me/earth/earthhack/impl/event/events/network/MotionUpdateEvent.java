package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.event.events.StageEvent;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

/**
 * Fired at the start of EntityPlayerSP.onUpdateWalkingPlayer
 * and at its end. Allows you to spoof the position, rotation
 * and onGround info that will be send to the server silently.
 *
 * This would be better as 2 events tbh, but I
 * had too many listeners for this event at that
 * point, so I just post the Stage.POST with
 * {@link
 * me.earth.earthhack.api.event.bus.api.EventBus#postReversed(Object, Class)}.
 */
@SuppressWarnings("unused")
public class MotionUpdateEvent extends StageEvent implements Globals
{
    private final double initialX;
    private final double initialY;
    private final double initialZ;
    private final float initialYaw;
    private final float initialPitch;
    private final boolean initialOnGround;

    private double x;
    private double y;
    private double z;
    private float rotationYaw;
    private float rotationPitch;
    private boolean onGround;
    protected boolean modified;
    private boolean pingBypass;

    public MotionUpdateEvent()
    {
        this(Stage.PRE, 0, 0, 0, 0, 0, false);
    }


    public MotionUpdateEvent(Stage stage, MotionUpdateEvent event)
    {
        this(stage,
             event.x,
             event.y,
             event.z,
             event.rotationYaw,
             event.rotationPitch,
             event.onGround);
    }

    public MotionUpdateEvent(Stage stage,
                             double x,
                             double y,
                             double z,
                             float rotationYaw,
                             float rotationPitch,
                             boolean onGround)
    {
        super(stage);
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.onGround = onGround;
        this.initialX = x;
        this.initialY = y;
        this.initialZ = z;
        this.initialYaw = rotationYaw;
        this.initialPitch = rotationPitch;
        this.initialOnGround = onGround;
    }

    public double getInitialX()
    {
        return initialX;
    }

    public double getInitialY()
    {
        return initialY;
    }

    public double getInitialZ()
    {
        return initialZ;
    }

    public float getInitialYaw()
    {
        return initialYaw;
    }

    public float getInitialPitch()
    {
        return initialPitch;
    }

    public boolean isInitialOnGround()
    {
        return initialOnGround;
    }

    public float getRotationYaw()
    {
        return rotationYaw;
    }

    public float getRotationPitch()
    {
        return rotationPitch;
    }

    public boolean isModified()
    {
        return modified;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.modified = true;
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.modified = true;
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.modified = true;
        this.z = z;
    }

    public float getYaw()
    {
        return rotationYaw;
    }

    public void setYaw(float rotationYaw)
    {
        this.modified = true;
        this.rotationYaw = rotationYaw;
    }

    public float getPitch()
    {
        return rotationPitch;
    }

    public void setPitch(float rotationPitch)
    {
        this.modified = true;
        this.rotationPitch = rotationPitch;
    }

    public boolean isOnGround()
    {
        return onGround;
    }

    public void setOnGround(boolean onGround)
    {
        this.modified = true;
        this.onGround = onGround;
    }

    public boolean isPingBypass() {
        return pingBypass;
    }

    public void setPingBypass(boolean pingBypass) {
        this.pingBypass = pingBypass;
    }

    /**
     * Fired in {@link EntityPlayerSP#onUpdate()}, when the player
     * is riding. X, Y, and Z can be set but it won't have any effect.
     * You can however retrieve the ridden Entity with
     * {@link Riding#getEntity()} and set its x, y, and z.
     */
    public static class Riding extends MotionUpdateEvent
    {
        private float moveStrafing;
        private float moveForward;
        private boolean jump;
        private boolean sneak;

        public Riding(Stage stage,
                      double x,
                      double y,
                      double z,
                      float rotationYaw,
                      float rotationPitch,
                      boolean onGround,
                      float moveStrafing,
                      float moveForward,
                      boolean jump,
                      boolean sneak)
        {
            super(stage, x, y, z, rotationYaw, rotationPitch, onGround);
            this.moveStrafing = moveStrafing;
            this.moveForward = moveForward;
            this.jump = jump;
            this.sneak = sneak;
        }

        public Riding(Stage stage, MotionUpdateEvent.Riding event)
        {
            this(stage,
                 event.getX(),
                 event.getY(),
                 event.getZ(),
                 event.getYaw(),
                 event.getPitch(),
                 event.isOnGround(),
                 event.moveStrafing,
                 event.moveForward,
                 event.jump,
                 event.sneak);
        }

        public Entity getEntity()
        {
            return mc.player.getLowestRidingEntity();
        }

        public float getMoveStrafing()
        {
            return moveStrafing;
        }

        public void setMoveStrafing(float moveStrafing)
        {
            this.modified = true;
            this.moveStrafing = moveStrafing;
        }

        public float getMoveForward()
        {
            return moveForward;
        }

        public void setMoveForward(float moveForward)
        {
            this.modified = true;
            this.moveForward = moveForward;
        }

        public boolean getJump()
        {
            return jump;
        }

        public void setJump(boolean jump)
        {
            this.modified = true;
            this.jump = jump;
        }

        public boolean getSneak()
        {
            return sneak;
        }

        public void setSneak(boolean sneak)
        {
            this.modified = true;
            this.sneak = sneak;
        }
    }

}
