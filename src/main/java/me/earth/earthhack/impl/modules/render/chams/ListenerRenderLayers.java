package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.render.RenderLayersEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderLayers extends ModuleListener<Chams, RenderLayersEvent>
{
    public ListenerRenderLayers(Chams module)
    {
        super(module, RenderLayersEvent.class);
    }

    @Override
    public void invoke(RenderLayersEvent event)
    {
        if (true) return; // we will need this later for stenciling
        if (module.isImageChams()
                && module.isValid(event.getEntity())
                && event.getEntity() != mc.getRenderViewEntity())
        {
            if (event.getStage() == Stage.PRE)
            {
                if (!module.renderLayers)
                {
                    event.setCancelled(true); // disable stencil buffer writing before layers are rendered so that depth is maintained
                }
                /*else
                {
                    glStencilFunc(GL_ALWAYS, 0, 0xFF); // write 0s to stencil buffer where the armor and held items will be rendered so that the iamge will be rendered around them (genius)
                    glStencilMask(0xFF);
                }*/
            }
        }
    }
}
