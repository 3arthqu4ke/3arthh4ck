package me.earth.earthhack.impl.core.ducks.render;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;

/**
 * Duck interface for {@link net.minecraft.client.renderer.RenderItem}.
 */
public interface IRenderItem
{
    void setNotRenderingEffectsInGUI(boolean render);

    void invokeRenderModel(IBakedModel model, ItemStack stack);
}
