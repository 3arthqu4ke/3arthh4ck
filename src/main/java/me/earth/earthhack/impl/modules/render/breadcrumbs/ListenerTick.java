package me.earth.earthhack.impl.modules.render.breadcrumbs;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

final class ListenerTick extends ModuleListener<BreadCrumbs, TickEvent>
{
    public ListenerTick(BreadCrumbs module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (event.isSafe() && module.timer.passed(module.delay.getValue()))
        {
            final Vec3d vec = mc.player.getPositionVector();
            if (vec.equals(BreadCrumbs.ORIGIN))
            {
                return;
            }

            if (module.trace == null)
            {
                module.trace = new Trace(0,
                        null,
                        mc.world.provider.getDimensionType(),
                        vec,
                        new ArrayList<>());
            }

            List<Trace.TracePos> trace = module.trace.getTrace();
            Vec3d vec3d = trace.isEmpty()
                                      ? vec
                                      : trace.get(trace.size() - 1).getPos();
            if (!trace.isEmpty()
                    && (vec.distanceTo(vec3d) > 100.0
                    || module.trace.getType() !=
                    mc.world.provider.getDimensionType()))
            {
                module.positions.add(module.trace);
                trace = new ArrayList<>();
                module.trace = new Trace(module.positions.size() + 1,
                        null,
                        mc.world.provider.getDimensionType(),
                        vec,
                        trace);
            }

            if (trace.isEmpty() || !vec.equals(vec3d))
            {
                trace.add(new Trace.TracePos(
                    vec, System.currentTimeMillis()
                            + module.fadeDelay.getValue()));
            }

            module.timer.reset();
        }
    }

}
