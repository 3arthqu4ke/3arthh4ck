package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.esp.mode.EspMode;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.mutables.BBRender;
import me.earth.earthhack.impl.util.render.mutables.MutableBB;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

final class ListenerRender extends ModuleListener<ESP, Render3DEvent>
{
    private final MutableBB bb = new MutableBB();

    public ListenerRender(ESP module)
    {
        super(module, Render3DEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (module.phase.getValue()) {
            for (EntityPlayer player : module.phasing) {
                // TODO: make the esp look nicer
                bb.setBB(
                    Interpolation.interpolateLastTickPos(
                        player.posX, player.lastTickPosX)
                        - 0.1
                        - mc.getRenderManager().viewerPosX,
                    Interpolation.interpolateLastTickPos(
                        player.posY, player.lastTickPosY)
                        - mc.getRenderManager().viewerPosY,
                    Interpolation.interpolateLastTickPos(
                        player.posZ, player.lastTickPosZ)
                        - 0.1
                        - mc.getRenderManager().viewerPosZ,
                    Interpolation.interpolateLastTickPos(
                        player.posX, player.lastTickPosX)
                        + 0.1
                        - mc.getRenderManager().viewerPosX,
                    Interpolation.interpolateLastTickPos(
                        player.posY, player.lastTickPosY)
                        - mc.getRenderManager().viewerPosY,
                    Interpolation.interpolateLastTickPos(
                        player.posZ, player.lastTickPosZ)
                        + 0.1
                        - mc.getRenderManager().viewerPosZ
                );

                BBRender.renderBox(bb, Color.RED, Color.RED, 1.5f);
            }
        }

        if (module.mode.getValue() == EspMode.Outline
            && module.storage.getValue()) {
            module.drawTileEntities();
        }

        if (module.items.getValue()) {
            final boolean fancyGraphics = mc.gameSettings.fancyGraphics;
            mc.gameSettings.fancyGraphics = false;
            ESP.isRendering = true;
            final float gammaSetting = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 100.0F;
            Entity renderEntity = RenderUtil.getEntity();
            Frustum frustum = Interpolation.createFrustum(renderEntity);
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityItem) || entity.isDead) {
                    continue;
                }

                AxisAlignedBB bb = entity.getEntityBoundingBox();
                if (!frustum.isBoundingBoxInFrustum(bb)) {
                    continue;
                }
                GL11.glPushMatrix();

                Vec3d i = Interpolation.interpolateEntity(entity);
                RenderUtil.drawNametag(
                        ((EntityItem) entity).getItem().getDisplayName(),
                        i.x, i.y, i.z,
                        module.scale.getValue(),
                        0xffffffff,
                        false);
                RenderUtil.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
            ESP.isRendering = false;
            mc.gameSettings.gammaSetting = gammaSetting;
            mc.gameSettings.fancyGraphics = fancyGraphics;
        }
    }
}
