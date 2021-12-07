package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.render.PostRenderEntitiesEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerRenderEntities
        extends ModuleListener<AutoCrystal, PostRenderEntitiesEvent>
{
    public ListenerRenderEntities(AutoCrystal module)
    {
        super(module, PostRenderEntitiesEvent.class);
    }

    @Override
    public void invoke(PostRenderEntitiesEvent event)
    {
        if (event.getPass() == 0)
        {
            module.crystalRender.render(event.getPartialTicks());
        }
    }

}
