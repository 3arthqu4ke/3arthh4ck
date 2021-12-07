package me.earth.earthhack.impl.modules.render.xray;

import me.earth.earthhack.impl.event.events.render.BlockLayerEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.util.BlockRenderLayer;

final class ListenerBlockLayer extends ModuleListener<XRay, BlockLayerEvent>
{
    public ListenerBlockLayer(XRay module)
    {
        super(module, BlockLayerEvent.class);
    }

    @Override
    public void invoke(BlockLayerEvent event)
    {
        if (!module.isValid(event.getBlock().getLocalizedName()))
        {
            event.setLayer(BlockRenderLayer.TRANSLUCENT);
        }
    }

}
