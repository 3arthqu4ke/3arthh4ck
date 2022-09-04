package me.earth.earthhack.impl.util.minecraft;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.player.spectate.EntityPlayerNoInterp;
import me.earth.earthhack.impl.modules.render.nametags.IEntityNoNametag;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;
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

    public EntityPlayer tracked;
    public volatile boolean active;
    public boolean shrinkPush;
    public boolean gravity;
    public double gravityFactor = 1.0;
    public double yPlusFactor = 1.0;
    public double yMinusFactor = 1.0;
    public int ticks;

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

    public void updateFromTrackedEntity()
    {
        this.motionX = tracked.motionX;
        this.motionY = tracked.motionY > 0.0 ? tracked.motionY * yPlusFactor : tracked.motionY * yMinusFactor;
        this.motionZ = tracked.motionZ;

        if (gravity) {
            this.motionY -= 0.03999999910593033D * gravityFactor * ticks; // * 0.9800000190734863D ?
        }

        List<AxisAlignedBB> list1 = this.world.getCollisionBoxes(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ));
        if (this.motionY != 0.0D)
        {
            int k = 0;

            for (int l = list1.size(); k < l; ++k)
            {
                this.motionY = list1.get(k).calculateYOffset(this.getEntityBoundingBox(), this.motionY);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, this.motionY, 0.0D));
        }

        if (this.motionX != 0.0D)
        {
            int j5 = 0;

            for (int l5 = list1.size(); j5 < l5; ++j5)
            {
                this.motionX = list1.get(j5).calculateXOffset(this.getEntityBoundingBox(), this.motionX);
            }

            if (this.motionX != 0.0D)
            {
                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(this.motionX, 0.0D, 0.0D));
            }
        }

        if (motionY != 0.0D)
        {
            int k5 = 0;

            for (int i6 = list1.size(); k5 < i6; ++k5)
            {
                this.motionZ = list1.get(k5).calculateZOffset(this.getEntityBoundingBox(), this.motionZ);
            }

            if (this.motionZ != 0.0D)
            {
                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, this.motionZ));
            }
        }

        this.resetPositionToBB();
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
