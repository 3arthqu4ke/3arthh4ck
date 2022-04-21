package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.esp.mode.EspMode;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

final class ListenerModel extends ModuleListener<ESP, ModelRenderEvent.Pre> {
    public ListenerModel(ESP module) {
        super(module, ModelRenderEvent.Pre.class);
    }

    @Override
    public void invoke(ModelRenderEvent.Pre event) {
        if (module.mode.getValue() == EspMode.Outline) {
            if (!module.isValid(event.getEntity())) {
                return;
            }

            render(event);

            final Color clr = module.getEntityColor(event.getEntity());
            module.renderOne(module.lineWidth.getValue());
            render(event);

            GlStateManager.glLineWidth(module.lineWidth.getValue());
            module.renderTwo();
            render(event);

            GlStateManager.glLineWidth(module.lineWidth.getValue());
            module.renderThree();
            module.renderFour(clr);
            render(event);
            GlStateManager.glLineWidth(module.lineWidth.getValue());
            module.renderFive();
            event.setCancelled(true);
        }
    }

    private void render(ModelRenderEvent.Pre event) {
        event.getModel().render(
                event.getEntity(),
                event.getLimbSwing(),
                event.getLimbSwingAmount(),
                event.getAgeInTicks(),
                event.getNetHeadYaw(),
                event.getHeadPitch(),
                event.getScale()
        );
    }

}