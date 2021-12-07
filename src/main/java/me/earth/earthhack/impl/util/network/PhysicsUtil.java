package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;

public class PhysicsUtil implements Globals
{
    public static void runPhysicsTick()
    {
        int lastSwing = ((IEntityLivingBase) mc.player)
                .getTicksSinceLastSwing();
        int useCount  = ((IEntityLivingBase) mc.player)
                .getActiveItemStackUseCount();

        int hurtTime              = mc.player.hurtTime;
        float prevSwingProgress   = mc.player.prevSwingProgress;
        float swingProgress       = mc.player.swingProgress;
        int swingProgressInt      = mc.player.swingProgressInt;
        boolean isSwingInProgress = mc.player.isSwingInProgress;
        float rotationYaw         = mc.player.rotationYaw;
        float prevRotationYaw     = mc.player.prevRotationYaw;
        float renderYawOffset     = mc.player.renderYawOffset;
        float prevRenderYawOffset = mc.player.prevRenderYawOffset;
        float rotationYawHead     = mc.player.rotationYawHead;
        float prevRotationYawHead = mc.player.prevRotationYawHead;
        float cameraYaw           = mc.player.cameraYaw;
        float prevCameraYaw       = mc.player.prevCameraYaw;
        float renderArmYaw        = mc.player.renderArmYaw;
        float prevRenderArmYaw    = mc.player.prevRenderArmYaw;
        float renderArmPitch      = mc.player.renderArmPitch;
        float prevRenderArmPitch  = mc.player.prevRenderArmPitch;
        float walk                = mc.player.distanceWalkedModified;
        float prevWalk            = mc.player.prevDistanceWalkedModified;
        double chasingPosX        = mc.player.chasingPosX;
        double prevChasingPosX    = mc.player.prevChasingPosX;
        double chasingPosY        = mc.player.chasingPosY;
        double prevChasingPosY    = mc.player.prevChasingPosY;
        double chasingPosZ        = mc.player.chasingPosZ;
        double prevChasingPosZ    = mc.player.prevChasingPosZ;
        float limbSwingAmount     = mc.player.limbSwingAmount;
        float prevLimbSwingAmount = mc.player.prevLimbSwingAmount;
        float limbSwing           = mc.player.limbSwing;

        ((IEntityPlayerSP) mc.player).superUpdate();

        ((IEntityLivingBase) mc.player)
                .setTicksSinceLastSwing(lastSwing);
        ((IEntityLivingBase) mc.player)
                .setActiveItemStackUseCount(useCount);

        mc.player.hurtTime                   = hurtTime;
        mc.player.prevSwingProgress          = prevSwingProgress;
        mc.player.swingProgress              = swingProgress;
        mc.player.swingProgressInt           = swingProgressInt;
        mc.player.isSwingInProgress          = isSwingInProgress;
        mc.player.rotationYaw                = rotationYaw;
        mc.player.prevRotationYaw            = prevRotationYaw;
        mc.player.renderYawOffset            = renderYawOffset;
        mc.player.prevRenderYawOffset        = prevRenderYawOffset;
        mc.player.rotationYawHead            = rotationYawHead;
        mc.player.prevRotationYawHead        = prevRotationYawHead;
        mc.player.cameraYaw                  = cameraYaw;
        mc.player.prevCameraYaw              = prevCameraYaw;
        mc.player.renderArmYaw               = renderArmYaw;
        mc.player.prevRenderArmYaw           = prevRenderArmYaw;
        mc.player.renderArmPitch             = renderArmPitch;
        mc.player.prevRenderArmPitch         = prevRenderArmPitch;
        mc.player.distanceWalkedModified     = walk;
        mc.player.prevDistanceWalkedModified = prevWalk;
        mc.player.chasingPosX                = chasingPosX;
        mc.player.prevChasingPosX            = prevChasingPosX;
        mc.player.chasingPosY                = chasingPosY;
        mc.player.prevChasingPosY            = prevChasingPosY;
        mc.player.chasingPosZ                = chasingPosZ;
        mc.player.prevChasingPosZ            = prevChasingPosZ;
        mc.player.limbSwingAmount            = limbSwingAmount;
        mc.player.prevLimbSwingAmount        = prevLimbSwingAmount;
        mc.player.limbSwing                  = limbSwing;

        ((IEntityPlayerSP) mc.player).invokeUpdateWalkingPlayer();
    }

}
