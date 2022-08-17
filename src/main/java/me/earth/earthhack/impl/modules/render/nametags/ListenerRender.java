package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

final class ListenerRender extends ModuleListener<Nametags, Render3DEvent>
{
    private int xOffset;
    private int maxEnchHeight;
    private boolean renderDurability;

    public ListenerRender(Nametags module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (!module.twoD.getValue()) {
            module.updateNametags();
            Entity renderEntity = RenderUtil.getEntity();
            Vec3d interp = Interpolation.interpolateEntity(renderEntity);
            Nametag.isRendering = true;
            for (Nametag nametag : module.nametags) {
                if (nametag.player.isDead
                        || nametag.player.isInvisible()
                            && !module.invisibles.getValue()
                        || module.withDistance.getValue()
                            && renderEntity.getDistanceSq(nametag.player)
                                > MathUtil.square(module.distance.getValue())
                        || module.fov.getValue()
                            && !RotationUtil.inFov(nametag.player) // Frustum?
                            && (!module.close.getValue() || renderEntity.getDistanceSq(nametag.player) > 1.0))
                {
                    continue;
                }

                Vec3d i = Interpolation.interpolateEntity(nametag.player);
                renderNametag(nametag, nametag.player, i.x, i.y, i.z, interp);
            }
            Nametag.isRendering = false;

            if (module.debug.getValue()) {
                Frustum frustum = Interpolation.createFrustum(renderEntity);
                // TODO: nametags for entities, like the 'Nether King'
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity == null
                            || EntityUtil.isDead(entity)
                            || entity instanceof EntityPlayer
                            || entity.isInvisible()
                                && !module.invisibles.getValue()
                            || module.withDistance.getValue()
                                && renderEntity.getDistanceSq(entity)
                                    > MathUtil.square(module.distance.getValue())
                            || module.fov.getValue()
                                && !frustum.isBoundingBoxInFrustum(entity.getRenderBoundingBox())
                                && (!module.close.getValue() || renderEntity.getDistanceSq(entity) > 1.0)) {
                        continue;
                    }

                    Vec3d i = Interpolation.interpolateEntity(entity);
                    RenderUtil.drawNametag(entity.getEntityId() + "",
                            i.x, i.y, i.z,
                            module.scale.getValue(),
                            0xffffffff,
                            false);
                }
            }
        }
    }

    private void renderNametag(Nametag nametag,
                               EntityPlayer player,
                               double x,
                               double y,
                               double z,
                               Vec3d mcPlayerInterpolation)
    {
        double yOffset = y + (player.isSneaking() ? 0.5D : 0.7D);
        double xDist = mcPlayerInterpolation.x - x;
        double yDist = mcPlayerInterpolation.y - y;
        double zDist = mcPlayerInterpolation.z - z;
        y = MathHelper.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);

        int nameWidth = nametag.nameWidth / 2;
        double scaling = 0.0018 + module.scale.getValue() * y;

        if (y <= 8.0)
        {
            scaling = 0.0245;
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();

        GlStateManager.translate((float) x,
                                 (float) yOffset + 1.4F,
                                 (float) z);

        GlStateManager.rotate(
                -mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);

        float xRot = mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;

        GlStateManager.rotate(
                mc.getRenderManager().playerViewX, xRot, 0.0f, 0.0f);

        GlStateManager.scale(-scaling, -scaling, scaling);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();

        RenderUtil.prepare(-nameWidth - 1,
                           -Managers.TEXT.getStringHeightI(),
                           nameWidth + 2,
                           1.0f,
                           1.8F,
                           0x55000400,
                           0x33000000);

        GlStateManager.disableBlend();

        Managers.TEXT.drawStringWithShadow(
                nametag.nameString,
                -nameWidth,
                -(Managers.TEXT.getStringHeightI() - 1),
                nametag.nameColor);

        xOffset = -nametag.stacks.size() * 8
                    - (nametag.mainHand == null ? 0 : 8);
        maxEnchHeight = nametag.maxEnchHeight;
        renderDurability = nametag.renderDura;
        GlStateManager.pushMatrix();

        if (nametag.mainHand != null)
        {
            renderStackRenderer(nametag.mainHand, true);
        }

        for (StackRenderer sr : nametag.stacks)
        {
            renderStackRenderer(sr, false);
        }

        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private void renderStackRenderer(StackRenderer sr, boolean main)
    {
        int fontOffset = module.getFontOffset(maxEnchHeight);
        if (module.armor.getValue())
        {
            sr.renderStack(xOffset, fontOffset, maxEnchHeight);
            fontOffset -= 32;
        }

        if (module.durability.getValue() && sr.isDamageable())
        {
            sr.renderDurability(xOffset, fontOffset);
            fontOffset -= Managers.TEXT.getStringHeightI();
        }
        else
        {
            if (renderDurability)
            {
                fontOffset -= Managers.TEXT.getStringHeightI();
            }
        }

        if (module.itemStack.getValue() && main)
        {
            sr.renderText(fontOffset);
        }

        if (module.armor.getValue()
                || module.durability.getValue()
                || sr.isDamageable())
        {
            xOffset += 16;
        }
    }

}
