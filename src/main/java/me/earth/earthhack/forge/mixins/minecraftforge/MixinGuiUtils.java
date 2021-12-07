package me.earth.earthhack.forge.mixins.minecraftforge;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiUtils.class, remap = false)
public abstract class MixinGuiUtils
{
    @Redirect(
        method = "drawHoveringText(Lnet/minecraft/item/ItemStack;Ljava/util/List;IIIIILnet/minecraft/client/gui/FontRenderer;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z",
            ordinal = 3))
    private static boolean postTextHook(EventBus eventBus, Event event)
    {
        RenderTooltipEvent.PostText e = (RenderTooltipEvent.PostText) event;
        Bus.EVENT_BUS.post(new ToolTipEvent.Post(e.getStack(), e.getX(), e.getY()));
        return eventBus.post(event);
    }

}
