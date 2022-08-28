package me.earth.earthhack.impl.modules.render.popchams;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.entity.StaticModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

final class ListenerRender extends ModuleListener<PopChams, Render3DEvent>
{

    public ListenerRender(PopChams module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        for (PopChams.PopData data : module.getPopDataList()) {
            EntityPlayer player = data.getPlayer();
            StaticModelPlayer model = data.getModel();
            double x = data.getX() - mc.getRenderManager().viewerPosX;
            double y = data.getY() - mc.getRenderManager().viewerPosY;
            y += module.yAnimations.getValue() * (System.currentTimeMillis() - data.getTime()) / module.fadeTime.getValue().doubleValue();
            double z = data.getZ() - mc.getRenderManager().viewerPosZ;

            GlStateManager.pushMatrix();
            RenderUtil.startRender();

            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(180 - model.getYaw(), 0, 1, 0);

            final Color boxColor = module.getColor(data.getPlayer());
            final Color outlineColor = module.getOutlineColor(data.getPlayer());
            final float maxBoxAlpha = boxColor.getAlpha();
            final float maxOutlineAlpha = outlineColor.getAlpha();
            final float alphaBoxAmount = maxBoxAlpha / module.fadeTime.getValue();
            final float alphaOutlineAmount = maxOutlineAlpha / module.fadeTime.getValue();
            final int fadeBoxAlpha = MathHelper.clamp((int)(alphaBoxAmount * (data.getTime() + module.fadeTime.getValue() - System.currentTimeMillis())),0,(int)maxBoxAlpha);
            final int fadeOutlineAlpha = MathHelper.clamp((int)(alphaOutlineAmount * (data.getTime() + module.fadeTime.getValue() - System.currentTimeMillis())),0,(int)maxOutlineAlpha);

            Color box = new Color(boxColor.getRed(),boxColor.getGreen(),boxColor.getBlue(),fadeBoxAlpha);
            Color out = new Color(outlineColor.getRed(),outlineColor.getGreen(),outlineColor.getBlue(),fadeOutlineAlpha);

            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            double widthX = player.getEntityBoundingBox().maxX - player.getRenderBoundingBox().minX + 1;
            double widthZ = player.getEntityBoundingBox().maxZ - player.getEntityBoundingBox().minZ + 1;

            GlStateManager.scale(widthX, player.height, widthZ);

            GlStateManager.translate(0.0F, -1.501F, 0.0F);

            RenderUtil.color(box);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            model.render(0.0625f);

            RenderUtil.color(out);
            GL11.glLineWidth(module.lineWidth.getValue());
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            model.render(0.0625f);

            RenderUtil.endRender();
            GlStateManager.popMatrix();

        }

        module.getPopDataList().removeIf(e -> e.getTime() + module.fadeTime.getValue() < System.currentTimeMillis());
    }

}
