package me.earth.earthhack.impl.util.minecraft;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.player.spectate.EntityPlayerNoInterp;
import me.earth.earthhack.impl.modules.render.nametags.IEntityNoNametag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class MotionTracker extends EntityPlayerNoInterp
    implements Globals, IEntityNoNametag
{
    public double extraPosX;
    public double extraPosY;
    public double extraPosZ;

    public double lastExtraPosX;
    public double lastExtraPosY;
    public double lastExtraPosZ;

    public PushMode shouldPushOutOfBlocks;

    public EntityPlayer tracked;
    public MovementInput movementInput; // represents the inputs of the player controlling the tracked entity
    public boolean safe; // set to true when enough information has been collected about the tracked player that we can safely assume stuff such as whether the player is flying
    public volatile boolean active;
    public boolean wasPhasing;
    public boolean shrinkPush;

    public MotionTracker(World worldIn, EntityPlayer from)
    {
        super(worldIn, new GameProfile(from.getGameProfile().getId(), "Motion-Tracker-" + from.getName()));
        this.tracked = from;
        this.setEntityId(from.getEntityId() * -1);
        this.copyLocationAndAnglesFrom(from);
    }

    @SuppressWarnings("unused")
    private MotionTracker(World worldIn) // to appease the minecraft development intellij plugin do not use >:(
    {
        super(worldIn, new GameProfile(UUID.randomUUID(), "Motion-Tracker"));
    }

    public void resetMotion()
    {
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
    }

    // TODO: this is kinda bad
    public void pushOutOfBlocks(PushMode mode)
    {
        AxisAlignedBB axisalignedbb = shrinkPush
            ? this.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625)
            : this.getEntityBoundingBox();
        // TODO: smarter way than calling this 4 times?????
        mode.pushOutOfBlocks(this, this.posX - (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
        mode.pushOutOfBlocks(this, this.posX - (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
        mode.pushOutOfBlocks(this, this.posX + (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
        mode.pushOutOfBlocks(this, this.posX + (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
    }

    public void detectWasPhasing()
    {
        wasPhasing = false;
        if (shouldPushOutOfBlocks != PushMode.None) {
            resetMotion();
            pushOutOfBlocks(shouldPushOutOfBlocks);
            wasPhasing = this.motionX != 0.0 || this.motionY != 0.0 || this.motionZ != 0.0;
        }
    }

    public void updateFromTrackedEntity()
    {
        this.motionX = tracked.motionX;
        this.motionY = tracked.motionY;
        this.motionZ = tracked.motionZ;
        this.posX += Math.abs(this.motionX) >= 0.1 ? this.motionX : 0.0;
        this.posY += Math.abs(this.motionY) >= 0.1 ? this.motionY : 0.0;
        this.posZ += Math.abs(this.motionZ) >= 0.1 ? this.motionZ : 0.0;
        this.setPosition(this.posX, this.posY, this.posZ);

        if (shouldPushOutOfBlocks != PushMode.None && !wasPhasing) {
            resetMotion();
            pushOutOfBlocks(shouldPushOutOfBlocks);
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.motionX = tracked.motionX;
            this.motionY = tracked.motionY;
            this.motionZ = tracked.motionZ;
            this.setPosition(this.posX, this.posY, this.posZ);
        }

        this.onGround = tracked.onGround;
        this.prevPosX = tracked.prevPosX;
        this.prevPosY = tracked.prevPosY;
        this.prevPosZ = tracked.prevPosZ;
        this.collided = tracked.collided;
        this.collidedHorizontally = tracked.collidedHorizontally;
        this.collidedVertically = tracked.collidedVertically;
        this.moveForward = tracked.moveForward;
        this.moveStrafing = tracked.moveStrafing;
        this.moveVertical = tracked.moveVertical;
        this.lastTickPosX = posX;
        this.lastTickPosY = posY;
        this.lastTickPosZ = posZ;
        this.lastExtraPosX = extraPosX;
        this.lastExtraPosY = extraPosY;
        this.lastExtraPosZ = extraPosZ;
    }

    @Override
    public void onUpdate()
    {
        // NOP
    }

    @Override
    public void onLivingUpdate()
    {
        // NOP
    }

    @Override
    public void setDead()
    {
        this.isDead = true;
        this.dead = true;
    }

    @Override
    public boolean isSpectator()
    {
        return false;
    }

    @Override
    public boolean isCreative()
    {
        return false;
    }

}
