package me.earth.earthhack.impl.modules.render.trails;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.BreadCrumbs;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

final class ListenerTick extends ModuleListener<Trails, TickEvent> {

    public ListenerTick(Trails module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (mc.world == null) return;
        if (module.ids.keySet().isEmpty()) return;
        for (Integer id : module.ids.keySet()) {
            if (id == null) continue;
            if (mc.world.loadedEntityList == null) return;
            if (mc.world.loadedEntityList.isEmpty()) return;
            /*if (module.traceLists.containsKey(id)) {
                for (Trace trace : module.traceLists.get(id)) {
                    for (Vec3d vec : trace.getTrace()) {
                        Earthhack.getLogger().info("trace " + module.traceLists.keySet().indexOf(trace) module.trace.getTrace().indexOf(vec)  ": " + vec.toString());
                    }
                }
            }*/
            /*if (module.traces.get(id) != null) {
                for (Vec3d vec : module.traces.get(id).getTrace()) {
                    Earthhack.getLogger().info(id + " trace " + module.traces.get(id).getTrace().indexOf(vec) + ": " + vec.toString());
                }
            }*/
            Trace idTrace = module.traces.get(id);
            Entity entity = mc.world.getEntityByID(id);
            if (entity != null) {
                Vec3d vec = entity.getPositionVector();
                if (vec == null) continue;
                if (vec.equals(BreadCrumbs.ORIGIN))
                {
                    continue;
                }

                if (!module.traces.containsKey(id) || idTrace == null)
                {
                    module.traces.put(id, new Trace(0,
                            null,
                            mc.world.provider.getDimensionType(),
                            vec,
                            new ArrayList<>()));
                    idTrace = module.traces.get(id);
                }

                List<Trace.TracePos> trace = idTrace.getTrace();
                Vec3d vec3d = trace.isEmpty() ? vec : trace.get(trace.size() - 1).getPos();
                if (!trace.isEmpty()
                        && (vec.distanceTo(vec3d) > 100.0
                        || idTrace.getType() !=
                        mc.world.provider.getDimensionType()))
                {
                    module.traceLists.get(id).add(idTrace);
                    trace = new ArrayList<>();
                    module.traces.put(id, new Trace(module.traceLists.get(id).size() + 1,
                            null,
                            mc.world.provider.getDimensionType(),
                            vec,
                            new ArrayList<>()));
                }

                if (trace.isEmpty() || !vec.equals(vec3d))
                {
                    trace.add(new Trace.TracePos(vec));
                }
            }

            TimeAnimation animation = module.ids.get(id);

            if (entity instanceof EntityArrow && (entity.onGround || entity.collided || !entity.isAirBorne)) {
                animation.play();
            }

            if (animation != null && module.color.getAlpha() - animation.getCurrent() <= 0/*animation.getCurrent() >= module.color.getAlpha()*/) {
                animation.stop();
                module.ids.remove(id);
                module.traceLists.remove(id);
                module.traces.remove(id);
            }
        }
    }

}
