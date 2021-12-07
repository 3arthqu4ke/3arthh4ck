package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.util.math.BlockPos;

final class ListenerRender extends ModuleListener<AntiSurround, Render3DEvent>
{
    public ListenerRender(AntiSurround module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (module.active.get() && module.drawEsp.getValue())
        {
            BlockPos pos = module.pos;
            if (pos != null)
            {
                module.esp.render(Interpolation.interpolatePos(pos, 1.0f));
            }
        }
    }

}
