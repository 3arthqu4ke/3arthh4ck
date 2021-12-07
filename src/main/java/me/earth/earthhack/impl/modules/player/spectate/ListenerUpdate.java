package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.MathHelper;

final class ListenerUpdate extends ModuleListener<Spectate, UpdateEvent>
{
    public ListenerUpdate(Spectate module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        // baritone could set this to its PlayerMovementInput
        if (mc.player.movementInput instanceof MovementInputFromOptions)
        {
            mc.player.movementInput = new MovementInput();
        }

        EntityPlayerNoInterp render = module.render;
        EntityPlayerNoInterp fake   = module.fakePlayer;

        fake.setPositionAndRotationDirect(
            mc.player.posX,
            mc.player.posY,
            mc.player.posZ,
            mc.player.rotationYaw,
            mc.player.rotationPitch,
            3,
            false);

        fake.setAbsorptionAmount(mc.player.getAbsorptionAmount());
        fake.setHealth(mc.player.getHealth());
        fake.hurtTime      = mc.player.hurtTime;
        fake.maxHurtTime   = mc.player.maxHurtTime;
        fake.attackedAtYaw = mc.player.attackedAtYaw;

        render.noClip = true;
        render.setAbsorptionAmount(mc.player.getAbsorptionAmount());
        render.setHealth(mc.player.getHealth());
        render.setAir(mc.player.getAir());
        render.getFoodStats().setFoodLevel(
                mc.player.getFoodStats().getFoodLevel());
        render.getFoodStats().setFoodSaturationLevel(
                mc.player.getFoodStats().getSaturationLevel());
        render.setVelocity(0, 0, 0);
        render.setPrimaryHand(mc.player.getPrimaryHand());
        render.hurtTime      = mc.player.hurtTime;
        render.maxHurtTime   = mc.player.maxHurtTime;
        render.attackedAtYaw = mc.player.attackedAtYaw;

        render.rotationYaw %= 360.0f;
        render.rotationPitch %= 360.0f;

        render.prevRotationYaw     = render.rotationYaw;
        render.prevRotationPitch   = render.rotationPitch;
        render.prevRotationYawHead = render.rotationYawHead;

        while (render.rotationYaw - render.prevRotationYaw < -180.0f)
        {
            render.prevRotationYaw -= 360.0f;
        }

        while (render.rotationYaw - render.prevRotationYaw >= 180.0f)
        {
            render.prevRotationYaw += 360.0f;
        }

        while (render.rotationPitch - render.prevRotationPitch < -180.0f)
        {
            render.prevRotationPitch -= 360.0f;
        }

        while (render.rotationPitch - render.prevRotationPitch >= 180.0f)
        {
            render.prevRotationPitch += 360.0f;
        }

        while (render.rotationYawHead - render.prevRotationYawHead < -180.0F)
        {
            render.prevRotationYawHead -= 360.0F;
        }

        while (render.rotationYawHead - render.prevRotationYawHead >= 180.0F)
        {
            render.prevRotationYawHead += 360.0F;
        }

        render.lastTickPosX      = render.posX;
        render.lastTickPosY      = render.posY;
        render.lastTickPosZ      = render.posZ;

        render.prevPosX          = render.posX;
        render.prevPosY          = render.posY;
        render.prevPosZ          = render.posZ;

        module.input.updatePlayerMoveState();
        double[] dir = MovementUtil.strafe(render, module.input, 0.5f);
        if (module.input.moveStrafe != 0 || module.input.moveForward != 0)
        {
            render.motionX = dir[0];
            render.motionZ = dir[1];
        }
        else
        {
            render.motionX = 0;
            render.motionZ = 0;
        }

        if (module.input.jump)
        {
            render.motionY += 0.5;
        }

        if (module.input.sneak)
        {
            render.motionY -= 0.5;
        }

        render.setEntityBoundingBox(
            render.getEntityBoundingBox()
                  .offset(render.motionX, render.motionY, render.motionZ));

        render.resetPositionToBB();

        render.chunkCoordX = MathHelper.floor(render.posX / 16.0);
        render.chunkCoordZ = MathHelper.floor(render.posZ / 16.0);
    }

}
