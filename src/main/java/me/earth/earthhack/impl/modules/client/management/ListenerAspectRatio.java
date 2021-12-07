package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.impl.event.events.render.AspectRatioEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerAspectRatio extends ModuleListener<Management, AspectRatioEvent> {
    public ListenerAspectRatio(Management module) {
        super(module, AspectRatioEvent.class);
    }

    @Override
    public void invoke(AspectRatioEvent event) {
        if (module.aspectRatio.getValue())
            event.setAspectRatio(module.aspectRatioWidth.getValue() / (float) module.aspectRatioHeight.getValue());
    }

}
