package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerGuiOpen extends ModuleListener<Auto32k, GuiScreenEvent<?>> {

    public ListenerGuiOpen(Auto32k module) {
        super(module, GuiScreenEvent.class);
    }

    @Override
    public void invoke(GuiScreenEvent<?> event) {
        module.onGui(event);
    }

}
