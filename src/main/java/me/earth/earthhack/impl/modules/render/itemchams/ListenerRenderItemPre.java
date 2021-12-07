package me.earth.earthhack.impl.modules.render.itemchams;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.render.RenderItemInFirstPersonEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderItemPre extends ModuleListener<ItemChams, RenderItemInFirstPersonEvent>
{

    public ListenerRenderItemPre(ItemChams module)
    {
        super(module, RenderItemInFirstPersonEvent.class);
    }

    @Override
    public void invoke(RenderItemInFirstPersonEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            if (!module.forceRender
                    && module.chams.getValue())
            {
                event.setCancelled(true);
            }
        }
    }

    private void render(RenderItemInFirstPersonEvent event)
    {
        mc.getItemRenderer().renderItemSide(
                event.getEntity(),
                event.getStack(),
                event.getTransformType(),
                event.isLeftHanded());
    }

}
