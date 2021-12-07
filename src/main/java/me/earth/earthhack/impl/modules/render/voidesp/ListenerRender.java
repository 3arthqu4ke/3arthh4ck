package me.earth.earthhack.impl.modules.render.voidesp;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.world.DimensionType;

final class ListenerRender extends ModuleListener<VoidESP, Render3DEvent>
{
    public ListenerRender(VoidESP module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (mc.player.dimension == DimensionType.THE_END.getId()
                || mc.player.posY > module.maxY.getValue())
        {
            return;
        }

        module.updateHoles();
        int holes = module.holes.getValue();
        for (int i = 0; i < holes && i < module.voidHoles.size(); i++)
        {
            module.renderPos(module.voidHoles.get(i));
        }
    }

}
