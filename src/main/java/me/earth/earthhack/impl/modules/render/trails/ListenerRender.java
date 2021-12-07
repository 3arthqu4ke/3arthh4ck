package me.earth.earthhack.impl.modules.render.trails;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;

final class ListenerRender extends ModuleListener<Trails, Render3DEvent> {

    public ListenerRender(Trails module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        for (Map.Entry<Integer, List<Trace>> entry : module.traceLists.entrySet()) {
            RenderUtil.startRender();
            GL11.glLineWidth(module.width.getValue());
            TimeAnimation animation = module.ids.get(entry.getKey());
            animation.add(event.getPartialTicks());
            GL11.glColor4f(module.color.getR(),
                    module.color.getG(),
                    module.color.getB(),
                    MathHelper.clamp((float) (module.color.getA() - animation.getCurrent() / 255.0f), 0 , 255));

            entry.getValue().forEach(trace ->
            {
                GL11.glBegin(GL11.GL_LINE_STRIP);
                trace.getTrace().forEach(this::renderVec);
                GL11.glEnd();
            });

            GL11.glColor4f(module.color.getR(),
                    module.color.getG(),
                    module.color.getB(),
                    MathHelper.clamp((float) (module.color.getA() - animation.getCurrent() / 255.0f), 0 , 255));

            GL11.glBegin(GL11.GL_LINE_STRIP);
            Trace trace = module.traces.get(entry.getKey());
            if (trace != null) {
                trace.getTrace().forEach(this::renderVec);
            }
            GL11.glEnd();
            RenderUtil.endRender();
        }
    }

    private void renderVec(Trace.TracePos tracePos)
    {
        double x = tracePos.getPos().x - Interpolation.getRenderPosX();
        double y = tracePos.getPos().y - Interpolation.getRenderPosY();
        double z = tracePos.getPos().z - Interpolation.getRenderPosZ();
        GL11.glVertex3d(x, y, z);
    }

}
