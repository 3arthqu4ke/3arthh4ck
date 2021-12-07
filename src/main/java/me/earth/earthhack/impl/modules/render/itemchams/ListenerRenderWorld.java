package me.earth.earthhack.impl.modules.render.itemchams;

import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.event.events.render.WorldRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.render.ItemShader;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.Display;

public class ListenerRenderWorld extends ModuleListener<ItemChams, WorldRenderEvent>
{
    public ListenerRenderWorld(ItemChams module)
    {
        super(module, WorldRenderEvent.class);
    }

    @Override
    public void invoke(WorldRenderEvent event)
    {
        if (Display.isActive() || Display.isVisible())
        {
            if (module.chams.getValue())
            {
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GlStateManager.enableAlpha();
                ItemShader shader = ItemShader.ITEM_SHADER;
                shader.blur = module.blur.getValue();
                shader.mix = module.mix.getValue();
                shader.alpha = module.chamColor.getValue().getAlpha() / 255.0f;
                shader.imageMix = module.imageMix.getValue();
                shader.useImage = module.useImage.getValue();
                shader.startDraw(mc.getRenderPartialTicks());
                module.forceRender = true;
                ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(mc.getRenderPartialTicks(), 2);
                module.forceRender = false;
                shader.stopDraw(module.chamColor.getValue(), module.radius.getValue(), 1.0f);
                GlStateManager.disableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.disableDepth();
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        }
    }
}
