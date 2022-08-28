package me.earth.earthhack.impl.modules.render.trajectories;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import java.util.List;

final class ListenerRender extends ModuleListener<Trajectories, Render3DEvent> {

    public ListenerRender(Trajectories module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (mc.player == null || mc.world == null || mc.gameSettings.thirdPersonView != 0)
            return;
        if (!((mc.player.getHeldItemMainhand() != ItemStack.EMPTY && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) || (mc.player.getHeldItemMainhand() != ItemStack.EMPTY && module.isThrowable(mc.player.getHeldItemMainhand().getItem())) || (mc.player.getHeldItemOffhand() != ItemStack.EMPTY && module.isThrowable(mc.player.getHeldItemOffhand().getItem()))))
            return;
        final double renderPosX = Interpolation.getRenderPosX();
        final double renderPosY = Interpolation.getRenderPosY();
        final double renderPosZ = Interpolation.getRenderPosZ();
        Item item = null;
        if (mc.player.getHeldItemMainhand() != ItemStack.EMPTY && (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow || module.isThrowable(mc.player.getHeldItemMainhand().getItem()))) {
            item = mc.player.getHeldItemMainhand().getItem();
        } else if (mc.player.getHeldItemOffhand() != ItemStack.EMPTY && module.isThrowable(mc.player.getHeldItemOffhand().getItem())) {
            item = mc.player.getHeldItemOffhand().getItem();
        }
        if (item == null) return;
        RenderUtil.startRender();
        double posX = renderPosX - MathHelper.cos(mc.player.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
        double posY = renderPosY + mc.player.getEyeHeight() - 0.1000000014901161;
        double posZ = renderPosZ - MathHelper.sin(mc.player.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
        final float maxDist = module.getDistance(item);
        double motionX = -MathHelper.sin(mc.player.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(mc.player.rotationPitch / 180.0f * 3.1415927f) * maxDist;
        double motionY = -MathHelper.sin((mc.player.rotationPitch - module.getThrowPitch(item)) / 180.0f * 3.141593f) * maxDist;
        double motionZ = MathHelper.cos(mc.player.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(mc.player.rotationPitch / 180.0f * 3.1415927f) * maxDist;
        int var6 = 72000 - mc.player.getItemInUseCount();
        float power = var6 / 20.0f;
        power = (power * power + power * 2.0f) / 3.0f;
        if (power > 1.0f) {
            power = 1.0f;
        }
        final float distance = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;

        final float pow = (item instanceof ItemBow ? (power * 2.0f) : 1.0f) * module.getThrowVelocity(item);
        motionX *= pow;
        motionY *= pow;
        motionZ *= pow;
        if (!mc.player.onGround)
            motionY += mc.player.motionY;

        GlStateManager.color(module.color.getValue().getRed() / 255.f, module.color.getValue().getGreen() / 255.f, module.color.getValue().getBlue() / 255.f, module.color.getValue().getAlpha() / 255.f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        final float size = (float) ((item instanceof ItemBow) ? 0.3 : 0.25);
        boolean hasLanded = false;
        Entity landingOnEntity = null;
        RayTraceResult landingPosition = null;
        GL11.glBegin(GL11.GL_LINE_STRIP);
        while (!hasLanded && posY > 0.0) {
            Vec3d present = new Vec3d(posX, posY, posZ);
            Vec3d future = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            RayTraceResult possibleLandingStrip = mc.world.rayTraceBlocks(present, future, false, true, false);
            if (possibleLandingStrip != null && possibleLandingStrip.typeOfHit != RayTraceResult.Type.MISS) {
                landingPosition = possibleLandingStrip;
                hasLanded = true;
            }
            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
            List<Entity> entities = module.getEntitiesWithinAABB(arrowBox.offset(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            for (Entity entity : entities) {
                if (entity.canBeCollidedWith() && entity != mc.player) {
                    float var7 = 0.3f;
                    AxisAlignedBB var8 = entity.getEntityBoundingBox().expand(var7, var7, var7);
                    RayTraceResult possibleEntityLanding = var8.calculateIntercept(present, future);
                    if (possibleEntityLanding == null) {
                        continue;
                    }
                    hasLanded = true;
                    landingOnEntity = entity;
                    landingPosition = possibleEntityLanding;
                }
            }
            if (landingOnEntity != null) {
                GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            final float motionAdjustment = 0.99f;
            motionX *= motionAdjustment;
            motionY *= motionAdjustment;
            motionZ *= motionAdjustment;
            motionY -= module.getGravity(item);
            drawLine3D(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
        }
        GL11.glEnd();
        if (module.landed.getValue() &&  landingPosition != null && landingPosition.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.translate(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
            final int side = landingPosition.sideHit.getIndex();
            if (side == 2) {
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            } else if (side == 3) {
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            } else if (side == 4) {
                GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            } else if (side == 5) {
                GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            }
            final Cylinder c = new Cylinder();
            GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
            c.setDrawStyle(GLU.GLU_SILHOUETTE);
            if (landingOnEntity != null) {
                GlStateManager.color(0.0f, 0.0f, 0.0f, 1.0f);
                GL11.glLineWidth(2.5f);
                c.draw(0.5f, 0.15f, 0.0f, 8, 1);
                GL11.glLineWidth(0.1f);
                GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
            }
            c.draw(0.5f, 0.15f, 0.0f, 8, 1);
        }
        RenderUtil.endRender();
    }


    public void drawLine3D(double var1, double var2, double var3) {
        GL11.glVertex3d(var1, var2, var3);
    }
}
