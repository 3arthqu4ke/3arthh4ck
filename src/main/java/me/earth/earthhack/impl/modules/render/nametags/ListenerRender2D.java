package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.render.GLUProjection;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;

final class ListenerRender2D extends ModuleListener<Nametags, Render2DEvent> {
    private int xOffset;
    private int maxEnchHeight;
    private boolean renderDurability;

    public ListenerRender2D(Nametags module) {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event) {
        if (module.twoD.getValue()) {
            module.updateNametags();
            Nametag.isRendering = true;
            Entity renderEntity = RenderUtil.getEntity();
            Frustum frustum = Interpolation.createFrustum(renderEntity);
            for (Nametag nametag : module.nametags) {
                if (nametag.player.isDead
                        || nametag.player.isInvisible()
                        && !module.invisibles.getValue()
                        || module.fov.getValue()
                        && !frustum.isBoundingBoxInFrustum(
                        nametag.player.getRenderBoundingBox()))
                {
                    continue;
                }

                renderNametag(nametag, nametag.player, event);
            }
            Nametag.isRendering = false;
        }
    }

    private void renderNametag(Nametag nametag,
                               EntityPlayer entity,
                               Render2DEvent event) {
        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ((IMinecraft) mc).getTimer().renderPartialTicks;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ((IMinecraft) mc).getTimer().renderPartialTicks;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ((IMinecraft) mc).getTimer().renderPartialTicks;
        final AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
        final Vector3d[] corners = {new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f)};
        GLUProjection.Projection result;
        final Vector4f transformed = new Vector4f(event.getResolution().getScaledWidth() * 2.0f, event.getResolution().getScaledHeight() * 2.0f, -1.0f, -1.0f);
        for (Vector3d vec : corners) {
            result = GLUProjection.getInstance().project(vec.x - mc.getRenderManager().viewerPosX, vec.y - mc.getRenderManager().viewerPosY, vec.z - mc.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, true);
            transformed.setX((float) Math.min(transformed.getX(), result.getX()));
            transformed.setY((float) Math.min(transformed.getY(), result.getY()));
            transformed.setW((float) Math.max(transformed.getW(), result.getX()));
            transformed.setZ((float) Math.max(transformed.getZ(), result.getY()));
        }
        final float x1 = transformed.x;
        final float w1 = transformed.w - x1;
        final float y1 = transformed.y;
        int nameWidth = nametag.nameWidth / 2;

        GlStateManager.pushMatrix();
        Managers.TEXT.drawStringWithShadow(
                nametag.nameString,
                (x1 + (w1 / 2)) - nameWidth,
                y1 - 3 - mc.fontRenderer.FONT_HEIGHT,
                nametag.nameColor);

        xOffset = -nametag.stacks.size() * 8
                - (nametag.mainHand == null ? 0 : 8);
        maxEnchHeight = nametag.maxEnchHeight;
        renderDurability = nametag.renderDura;
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        if (nametag.mainHand != null) {
            renderStackRenderer(nametag.mainHand, x1 + (w1 / 2), y1 - 3, true);
        }

        for (StackRenderer sr : nametag.stacks) {
            renderStackRenderer(sr, x1 + (w1 / 2), y1 - 3, false);
        }
        GlStateManager.popMatrix();

    }

    private void renderStackRenderer(StackRenderer sr, float x, float y, boolean main) {
        int fontOffset = module.getFontOffset(maxEnchHeight);
        if (module.armor.getValue()) {
            sr.renderStack2D((int) x + xOffset, (int) y + fontOffset, maxEnchHeight);
            fontOffset -= 32;
        }

        if (module.durability.getValue() && sr.isDamageable()) {
            sr.renderDurability(x + xOffset, (y * 2) + fontOffset);
            fontOffset -= Managers.TEXT.getStringHeight();
        } else {
            if (renderDurability) {
                fontOffset -= Managers.TEXT.getStringHeight();
            }
        }

        if (module.itemStack.getValue() && main) {
            sr.renderText(x * 2, (y * 2) + fontOffset);
        }

        if (module.armor.getValue()
                || module.durability.getValue()
                || sr.isDamageable()) {
            xOffset += 16;
        }
    }
}
