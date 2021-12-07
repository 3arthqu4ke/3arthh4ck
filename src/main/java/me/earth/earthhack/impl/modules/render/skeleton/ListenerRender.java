package me.earth.earthhack.impl.modules.render.skeleton;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

final class ListenerRender extends ModuleListener<Skeleton, Render3DEvent>
{
    private static final ModuleCache<KillAura> KILL_AURA =
            Caches.getModule(KillAura.class);

    public ListenerRender(Skeleton module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        boolean lightning  = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean blend      = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean texture    = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean depth      = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean lineSmooth = GL11.glIsEnabled(GL11.GL_LINE_SMOOTH);

        if (lightning)
        {
            GL11.glDisable(GL11.GL_LIGHTING);
        }

        if (!blend)
        {
            GL11.glEnable(GL11.GL_BLEND);
        }

        GL11.glLineWidth(1.0f);
        if (texture)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        if (depth)
        {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        if (!lineSmooth)
        {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
        }

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.depthMask(false);
        List<EntityPlayer> playerEntities = mc.world.playerEntities;
        Entity renderEntity = RenderUtil.getEntity();
        module.rotations.keySet().removeIf(player -> player == null
                || !playerEntities.contains(player)
                || player.equals(renderEntity)
                || player.isPlayerSleeping()
                || EntityUtil.isDead(player));

        playerEntities.forEach(player -> //TODO: some nice for loops?
        {
            float[][] rotations = module.rotations.get(player);
            if (rotations != null)
            {
                GlStateManager.pushMatrix();

                if (Managers.FRIENDS.contains(player.getName()))
                {
                    final Color friendClr = module.friendColor.getValue();
                    GlStateManager.color(friendClr.getRed() / 255.f, friendClr.getGreen() / 255.f, friendClr.getBlue() / 255.f, friendClr.getAlpha() / 255.f);
                }
                else
                {
                    EntityPlayer autoCrystal = Managers.TARGET.getAutoCrystal();
                    Entity killAuraTarget    = KILL_AURA
                            .returnIfPresent(KillAura::getTarget, null);

                    if (player.equals(autoCrystal) || player.equals(killAuraTarget))
                    {
                        final Color targetClr = module.targetColor.getValue();
                        GlStateManager.color(targetClr.getRed() / 255.f,targetClr.getGreen() / 255.f,targetClr.getBlue() / 255.f,targetClr.getAlpha() / 255.f);
                    }
                    else
                    {
                        final Color clr = module.color.getValue();
                        GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, clr.getAlpha() / 255.f);
                    }
                }

                Vec3d interpolation = Interpolation.interpolateEntity(player);
                double pX = interpolation.x;
                double pY = interpolation.y;
                double pZ = interpolation.z;

                GlStateManager.translate(pX, pY , pZ);
                GlStateManager.rotate(-player.renderYawOffset, 0.0f, 1.0f, 0.0f);
                GlStateManager.translate(0.0, 0.0, player.isSneaking() ? -0.235 : 0.0);
                float sneak = player.isSneaking() ? 0.6f : 0.75f;

                GlStateManager.pushMatrix();
                GlStateManager.translate(-0.125, sneak, 0.0);

                if (rotations[3][0] != 0.0f)
                {
                    GlStateManager.rotate(rotations[3][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }

                if (rotations[3][1] != 0.0f)
                {
                    GlStateManager.rotate(rotations[3][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
                }

                if (rotations[3][2] != 0.0f)
                {
                    GlStateManager.rotate(rotations[3][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
                }

                GlStateManager.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -sneak, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.125, sneak, 0.0);
                if (rotations[4][0] != 0.0f)
                {
                    GlStateManager.rotate(rotations[4][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }

                if (rotations[4][1] != 0.0f)
                {
                    GlStateManager.rotate(rotations[4][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
                }

                if (rotations[4][2] != 0.0f)
                {
                    GlStateManager.rotate(rotations[4][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
                }

                GlStateManager.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -sneak, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.translate(0.0, 0.0, player.isSneaking() ? 0.25 : 0.0);
                GlStateManager.pushMatrix();
                double sneakOffset = 0.0;
                if (player.isSneaking())
                {
                    sneakOffset = -0.05;
                }

                GlStateManager.translate(0.0, sneakOffset, player.isSneaking() ? -0.01725 : 0.0);
                GlStateManager.pushMatrix();
                GlStateManager.translate(-0.375, sneak + 0.55, 0.0);
                if (rotations[1][0] != 0.0f)
                {
                    GlStateManager.rotate(rotations[1][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }

                if (rotations[1][1] != 0.0f)
                {
                    GlStateManager.rotate(rotations[1][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
                }

                if (rotations[1][2] != 0.0f)
                {
                    GlStateManager.rotate(-rotations[1][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
                }

                GlStateManager.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -0.5, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.375, sneak + 0.55, 0.0);

                if (rotations[2][0] != 0.0f)
                {
                    GlStateManager.rotate(rotations[2][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }

                if (rotations[2][1] != 0.0f)
                {
                    GlStateManager.rotate(rotations[2][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
                }

                if (rotations[2][2] != 0.0f)
                {
                    GlStateManager.rotate(-rotations[2][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
                }

                GlStateManager.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -0.5, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0, sneak + 0.55, 0.0);

                if (rotations[0][0] != 0.0f)
                {
                    GlStateManager.rotate(rotations[0][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }

                GlStateManager.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, 0.3, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.popMatrix();
                GlStateManager.rotate(player.isSneaking() ? 25.0f : 0.0f, 1.0f, 0.0f, 0.0f);

                if (player.isSneaking())
                {
                    sneakOffset = -0.16175;
                }

                GlStateManager.translate(0.0, sneakOffset, player.isSneaking() ? -0.48025 : 0.0);
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0, sneak, 0.0);
                GlStateManager.glBegin(3);
                GL11.glVertex3d(-0.125, 0.0, 0.0);
                GL11.glVertex3d(0.125, 0.0, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0, sneak, 0.0);
                GlStateManager.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, 0.55, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0, sneak + 0.55, 0.0);
                GlStateManager.glBegin(3);

                GL11.glVertex3d(-0.375, 0.0, 0.0);
                GL11.glVertex3d(0.375, 0.0, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.popMatrix();
            }
        });

        GlStateManager.depthMask(true);
        if (!lineSmooth)
        {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        if (depth)
        {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        if (texture)
        {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        if (!blend)
        {
            GL11.glDisable(GL11.GL_BLEND);
        }

        if (lightning)
        {
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

}
