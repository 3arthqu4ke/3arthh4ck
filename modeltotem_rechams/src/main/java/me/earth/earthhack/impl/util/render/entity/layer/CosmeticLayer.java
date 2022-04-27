package me.earth.earthhack.impl.util.render.entity.layer;

import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;

public class CosmeticLayer implements LayerRenderer<EntityPlayer>
{

    @Override
    public void doRenderLayer(EntityPlayer entitylivingbaseIn,
                              float limbSwing,
                              float limbSwingAmount,
                              float partialTicks,
                              float ageInTicks,
                              float netHeadYaw,
                              float headPitch,
                              float scale)
    {

    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }

}
