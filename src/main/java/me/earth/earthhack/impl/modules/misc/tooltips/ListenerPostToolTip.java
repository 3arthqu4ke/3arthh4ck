package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.world.storage.MapData;

final class ListenerPostToolTip extends
        ModuleListener<ToolTips, ToolTipEvent.Post>
{
    public ListenerPostToolTip(ToolTips module)
    {
        super(module, ToolTipEvent.Post.class);
    }

    @Override
    public void invoke(ToolTipEvent.Post event)
    {
        if (module.maps.getValue()
                && !event.getStack().isEmpty()
                && event.getStack().getItem() instanceof ItemMap)
        {
            MapData mapData =
                    Items.FILLED_MAP.getMapData(event.getStack(), mc.world);

            if (mapData != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f);
                RenderHelper.disableStandardItemLighting();
                mc.getTextureManager().bindTexture(ToolTips.MAP);
                Tessellator instance = Tessellator.getInstance();
                BufferBuilder buffer = instance.getBuffer();

                GlStateManager.translate(event.getX(),
                        event.getY() - 135.0f * 0.5f - 5.0f,
                        0.0f);

                GlStateManager.scale(0.5f, 0.5f, 0.5f);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
                buffer.pos(-7, 135.0f, 0.0).tex(0.0, 1.0).endVertex();
                buffer.pos(135.0f, 135.0f, 0.0).tex(1.0, 1.0).endVertex();
                buffer.pos(135.0f, -7, 0.0).tex(1.0, 0.0).endVertex();
                buffer.pos(-7, -7, 0.0).tex(0.0, 0.0).endVertex();
                instance.draw();

                mc.entityRenderer.getMapItemRenderer()
                        .renderMap(mapData, false);

                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
            }
        }
    }

}
