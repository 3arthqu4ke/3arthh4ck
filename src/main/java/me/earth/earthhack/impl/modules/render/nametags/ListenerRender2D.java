package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.GLUProjection;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Vector4f;

final class ListenerRender2D extends ModuleListener<Nametags, Render2DEvent>
{
    private int xOffset;
    private int maxEnchHeight;
    private boolean renderDurability;

    public ListenerRender2D(Nametags module)
    {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event)
    {
        Entity renderEntity;
        if (module.twoD.getValue()
            && mc.player != null
            && mc.world != null
            && (renderEntity = RenderUtil.getEntity()) != null)
        {
            module.updateNametags();
            Nametag.isRendering = true;
            ScaledResolution scaledResolution = event.getResolution();
            for (Nametag nametag : module.nametags)
            {
                if (nametag.player.isDead
                    || nametag.player.isInvisible() && !module.invisibles.getValue()
                    || module.withDistance.getValue()
                        && renderEntity.getDistanceSq(nametag.player)
                            > MathUtil.square(module.distance.getValue())
                    || module.fov.getValue()
                        && !RenderUtil.isInFrustum(nametag.player.getEntityBoundingBox())
                        && (!module.close.getValue() || mc.player.getDistanceSq(nametag.player) > 1.0))
                {
                    continue;
                }

                renderNametag(nametag, nametag.player, scaledResolution);
            }

            Nametag.isRendering = false;
        }
    }

    private void renderNametag(Nametag nametag,
                               EntityPlayer entity,
                               ScaledResolution scaledResolution)
    {
        final AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
        Vec3d pos = Interpolation.interpolateEntityNoRenderPos(entity);
        final Vec3d vec = new Vec3d(bb.maxX - bb.minX - entity.width / 2.0f, bb.maxY - bb.minY, bb.maxZ - bb.minZ - entity.width / 2.0f);
        final Vector4f transformed = new Vector4f(
                -1,
                -1,
                scaledResolution.getScaledHeight() + 1,
                scaledResolution.getScaledWidth() + 1);

        GLUProjection.Projection result = GLUProjection.getInstance().project(
                pos.x + vec.x - mc.getRenderManager().viewerPosX,
                pos.y + vec.y - mc.getRenderManager().viewerPosY,
                pos.z + vec.z - mc.getRenderManager().viewerPosZ,
                GLUProjection.ClampMode.DIRECT, true);

        if (result.isType(GLUProjection.Projection.Type.FAIL))
        {
            return;
        }

        transformed.setX((float) Math.max(transformed.getX(), result.getX()));
        transformed.setY((float) Math.max(transformed.getY(), result.getY()));
        transformed.setW((float) Math.min(transformed.getW(), result.getX()));
        transformed.setZ((float) Math.min(transformed.getZ(), result.getY()));

        if (transformed.getX() < 0
                || transformed.getY() < 0
                || transformed.getW() > scaledResolution.getScaledWidth()
                || transformed.getZ() > scaledResolution.getScaledHeight())
        {
            return;
        }

        final float x1 = transformed.x;
        final float w1 = transformed.w - x1;
        final float h1 = transformed.z;
        int nameWidth = nametag.nameWidth / 2;

        GlStateManager.pushMatrix();

        Managers.TEXT.drawStringWithShadow(
                nametag.nameString,
                (x1 + (w1 / 2)) - nameWidth,
                h1 - 3 - mc.fontRenderer.FONT_HEIGHT,
                nametag.nameColor);

        xOffset = -nametag.stacks.size() * 8
                - (nametag.mainHand == null ? 0 : 8);
        maxEnchHeight = nametag.maxEnchHeight;
        renderDurability = nametag.renderDura;
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        if (nametag.mainHand != null)
        {
            renderStackRenderer(nametag.mainHand, x1 + (w1 / 2), h1 - 3, true);
        }

        for (StackRenderer sr : nametag.stacks)
        {
            renderStackRenderer(sr, x1 + (w1 / 2), h1 - 3, false);
        }

        GlStateManager.popMatrix();
    }

    private void renderStackRenderer(StackRenderer sr, float x, float y, boolean main)
    {
        int fontOffset = module.getFontOffset(maxEnchHeight);
        if (module.armor.getValue())
        {
            sr.renderStack2D((int) x + xOffset, (int) y + fontOffset, maxEnchHeight);
            fontOffset -= 32;
        }

        if (module.durability.getValue() && sr.isDamageable())
        {
            sr.renderDurability(x + xOffset, (y * 2) + fontOffset);
            fontOffset -= Managers.TEXT.getStringHeightI();
        } else
        {
            if (renderDurability)
            {
                fontOffset -= Managers.TEXT.getStringHeightI();
            }
        }

        if (module.itemStack.getValue() && main)
        {
            sr.renderText(x * 2, (y * 2) + fontOffset);
        }

        if (module.armor.getValue()
                || module.durability.getValue()
                || sr.isDamageable())
        {
            xOffset += 16;
        }
    }
}
