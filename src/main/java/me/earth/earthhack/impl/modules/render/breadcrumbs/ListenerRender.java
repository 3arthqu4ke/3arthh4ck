package me.earth.earthhack.impl.modules.render.breadcrumbs;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.breadcrumbs.util.Trace;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

final class ListenerRender extends ModuleListener<BreadCrumbs, Render3DEvent> {
    public ListenerRender(BreadCrumbs module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (module.render.getValue()) {
            if (module.trace != null) {
                RenderUtil.startRender();
                GL11.glLineWidth(module.width.getValue());

                module.positions.forEach(trace ->
                {
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    trace.getTrace().forEach(this::renderVec);
                    GL11.glEnd();
                    if (module.fade.getValue())
                        trace.getTrace().removeIf(Trace.TracePos::shouldRemoveTrace);
                });

                GL11.glBegin(GL11.GL_LINE_STRIP);
                module.trace.getTrace().forEach(this::renderVec);
                if (module.fade.getValue())
                    module.trace.getTrace().removeIf(Trace.TracePos::shouldRemoveTrace);
                GL11.glEnd();
                RenderUtil.endRender();
            }
        }
    }

    private void renderVec(Trace.TracePos tracePos) {
        double x = tracePos.getPos().x - Interpolation.getRenderPosX();
        double y = tracePos.getPos().y - Interpolation.getRenderPosY();
        double z = tracePos.getPos().z - Interpolation.getRenderPosZ();
        final float percentage = module.fade.getValue() ? MathHelper.clamp(((module.color.getA()) / module.fadeDelay.getValue()) * (tracePos.getTime() - System.currentTimeMillis()), 0.f, module.color.getB()) /  module.color.getB() : module.color.getA();
        GL11.glColor4f(module.color.getR(),
                module.color.getG(),
                module.color.getB(),
                percentage);
        GL11.glVertex3d(x, y, z);
    }

}
