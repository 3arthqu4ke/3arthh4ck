package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.impl.event.events.render.CrystalRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.esp.mode.EspMode;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

import java.awt.*;

// TODO: YES, this is duplicate code, see ListenerRender
final class ListenerRenderCrystal
    extends ModuleListener<ESP, CrystalRenderEvent.Pre> {
    public ListenerRenderCrystal(ESP module) {
        super(module, CrystalRenderEvent.Pre.class);
    }

    @Override
    public void invoke(CrystalRenderEvent.Pre event) {
        Entity renderEntity;
        if (module.mode.getValue() == EspMode.Outline
            && module.misc.getValue()
            && event.getEntity() != null
            && !event.getEntity().isDead
            && (renderEntity = RenderUtil.getEntity()) != null
            && !renderEntity.equals(event.getEntity())) {
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

    private void render(CrystalRenderEvent.Pre event) {
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
