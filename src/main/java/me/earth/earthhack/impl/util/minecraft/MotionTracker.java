package me.earth.earthhack.impl.util.minecraft;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class MotionTracker extends EntityPlayer implements Globals
{
    public EntityPlayer tracked;
    public MovementInput movementInput; // represents the inputs of the player controlling the tracked entity
    public boolean safe; // set to true when enough information has been collected about the tracked player that we can safely assume stuff such as whether the player is flying

    public MotionTracker(World worldIn, EntityPlayer from)
    {
        super(worldIn, new GameProfile(UUID.randomUUID(), "Motion-Tracker-" + from.getName()));
        this.tracked = from;
        this.setEntityId(from.getEntityId() * -1);
        updateFromTrackedEntity();
    }

    public MotionTracker(World worldIn, MotionTracker from)
    {
        this(worldIn, from.tracked);
        this.movementInput = from.movementInput;
        this.safe = from.safe;
    }

    private MotionTracker(World worldIn) // to appease the minecraft development intellij plugin do not use >:(
    {
        super(worldIn, new GameProfile(UUID.randomUUID(), "Motion-Tracker"));
    }

    public void onUpdate()
    {
        updateFromTrackedEntity();
        super.onUpdate();
    }

    public void updateSilent()
    {
        super.onUpdate();
    }

    public void onLivingUpdate()
    {
        this.movementInput.updatePlayerMoveState();
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        axisalignedbb = this.getEntityBoundingBox();
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, axisalignedbb.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
        super.onLivingUpdate();
    }

    public void updateFromTrackedEntity()
    {
        this.movementInput = new MovementInputFromRemotePlayer(tracked);
        this.movementInput.updatePlayerMoveState();
        this.copyLocationAndAnglesFrom(tracked);
        this.setEntityBoundingBox(tracked.getEntityBoundingBox());
        this.motionX = tracked.motionX;
        this.motionY = tracked.motionY;
        this.motionZ = tracked.motionZ;
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
