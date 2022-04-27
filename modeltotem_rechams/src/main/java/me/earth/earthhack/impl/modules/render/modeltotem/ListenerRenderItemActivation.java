package me.earth.earthhack.impl.modules.render.modeltotem;

import me.earth.earthhack.impl.event.events.render.RenderItemActivationEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;

public class ListenerRenderItemActivation
        extends ModuleListener<ModelTotem, RenderItemActivationEvent>
{

    public ListenerRenderItemActivation(ModelTotem module)
    {
        super(module, RenderItemActivationEvent.class);
    }

    @Override
    public void invoke(RenderItemActivationEvent event)
    {
        if (event.getStack().getItem() == Items.TOTEM_OF_UNDYING) // in vanilla this is always true but who knows /shrug
        {
            GlStateManager.pushMatrix();
            event.setCancelled(true);
            GlStateManager.translate(module.popTranslateX.getValue(), module.popTranslateY.getValue(), module.popTranslateZ.getValue());
            GlStateManager.scale(module.popScaleX.getValue(), module.popScaleY.getValue(), module.popScaleZ.getValue());
            GlStateManager.rotate(module.popRotateHorizontal.getValue(), 1, 0, 0);
            GlStateManager.rotate(module.popRotateVertical.getValue(), 0, 1, 0);
            GlStateManager.rotate(module.popRotateZ.getValue(), 0, 0, 1);
            if (module.fileSettingTest.getValue().getMeshes().length != 0)
            {
                module.fileSettingTest.getValue().render(0, 0, 0, mc.getRenderPartialTicks());
            }
            GlStateManager.rotate(-module.popRotateZ.getValue(), 0, 0, 1);
            GlStateManager.rotate(-module.popRotateVertical.getValue(), 0, 1, 0);
            GlStateManager.rotate(-module.popRotateHorizontal.getValue(), 1, 0, 0);
            GlStateManager.scale(1 / module.popScaleX.getValue(), 1 / module.popScaleY.getValue(), 1 / module.popScaleZ.getValue());
            GlStateManager.translate(-module.popTranslateX.getValue(), -module.popTranslateY.getValue(), -module.popTranslateZ.getValue());
            GlStateManager.popMatrix();
        }
    }

}
