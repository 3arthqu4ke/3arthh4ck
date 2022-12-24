package me.earth.earthhack.impl.util.render.entity;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class StaticModelPlayer extends ModelPlayer implements Globals {
    private final EntityPlayer player;
    private float limbSwing;
    private float limbSwingAmount;
    private float yaw;
    private float yawHead;
    private float pitch;

    public StaticModelPlayer(EntityPlayer playerIn, boolean smallArms, float modelSize) {
        super(modelSize, smallArms);
        this.player = playerIn;
        this.limbSwing = player.limbSwing;
        this.limbSwingAmount = player.limbSwingAmount;
        this.yaw = player.rotationYaw;
        this.yawHead = player.rotationYawHead;
        this.pitch = player.rotationPitch;
        this.isSneak = player.isSneaking();
        this.rightArmPose = getArmPose(player, player.getPrimaryHand() == EnumHandSide.RIGHT ? player.getHeldItemMainhand() : player.getHeldItemOffhand());
        this.leftArmPose = getArmPose(player, player.getPrimaryHand() == EnumHandSide.RIGHT ? player.getHeldItemOffhand() : player.getHeldItemMainhand());
        this.swingProgress = player.swingProgress;
        this.setLivingAnimations(player, limbSwing, limbSwingAmount, mc.getRenderPartialTicks());
    }

    public void render(float scale) {
        this.render(player, limbSwing, limbSwingAmount, player.ticksExisted, yawHead, pitch, scale);
    }

    public void disableArmorLayers() {
        this.bipedBodyWear.showModel = false;
        this.bipedLeftLegwear.showModel = false;
        this.bipedRightLegwear.showModel = false;
        this.bipedLeftArmwear.showModel = false;
        this.bipedRightArmwear.showModel = false;
        this.bipedHeadwear.showModel = true;
        this.bipedHead.showModel = false;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public void setLimbSwing(float limbSwing) {
        this.limbSwing = limbSwing;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public void setLimbSwingAmount(float limbSwingAmount) {
        this.limbSwingAmount = limbSwingAmount;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getYawHead() {
        return yawHead;
    }

    public void setYawHead(float yawHead) {
        this.yawHead = yawHead;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    private static ModelBiped.ArmPose getArmPose(EntityPlayer player, ItemStack stack) {
        if (stack.isEmpty()) {
            return ModelBiped.ArmPose.EMPTY;
        }
        if (stack.getItem() instanceof ItemBow && player.getItemInUseCount() > 0) {
            return ModelBiped.ArmPose.BOW_AND_ARROW;
        }
        return ModelBiped.ArmPose.ITEM;
    }

}
