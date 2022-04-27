package me.earth.earthhack.impl.modules.render.modeltotem;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.render.RenderItemInFirstPersonEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;

public class ListenerRenderItemInFirstPerson
        extends ModuleListener<ModelTotem, RenderItemInFirstPersonEvent>
{

    public ListenerRenderItemInFirstPerson(ModelTotem module)
    {
        super(module, RenderItemInFirstPersonEvent.class);
    }

    @Override
    public void invoke(RenderItemInFirstPersonEvent event)
    {
        if (event.getStage() == Stage.PRE
                && event.getStack().getItem() == Items.TOTEM_OF_UNDYING)
        {
            GlStateManager.pushMatrix();
            event.setCancelled(true);
            GlStateManager.translate(module.translateX.getValue(), module.translateY.getValue(), module.translateZ.getValue());
            GlStateManager.scale(module.scaleX.getValue(), module.scaleY.getValue(), module.scaleZ.getValue());
            GlStateManager.rotate(module.rotateHorizontal.getValue(), 1, 0, 0);
            GlStateManager.rotate(module.rotateVertical.getValue(), 0, 1, 0);
            GlStateManager.rotate(module.rotateZ.getValue(), 0, 0, 1);
            if (module.fileSettingTest.getValue().getMeshes().length != 0)
            {
                module.fileSettingTest.getValue().render(0, 0, 0, mc.getRenderPartialTicks());
            }
            GlStateManager.rotate(-module.rotateZ.getValue(), 0, 0, 1);
            GlStateManager.rotate(-module.rotateVertical.getValue(), 0, 1, 0);
            GlStateManager.rotate(-module.rotateHorizontal.getValue(), 1, 0, 0);
            GlStateManager.scale(1 / module.scaleX.getValue(), 1 / module.scaleY.getValue(), 1 / module.scaleZ.getValue());
            GlStateManager.translate(-module.translateX.getValue(), -module.translateY.getValue(), -module.translateZ.getValue());
            GlStateManager.popMatrix();
        }
    }

}
