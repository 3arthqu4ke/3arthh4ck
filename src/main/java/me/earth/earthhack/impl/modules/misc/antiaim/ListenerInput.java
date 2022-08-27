package me.earth.earthhack.impl.modules.misc.antiaim;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerInput extends ModuleListener<AntiAim, MovementInputEvent> {
    private boolean sneak;

    public ListenerInput(AntiAim module) {
        super(module, MovementInputEvent.class, 10_000);
    }

    @Override
    public void invoke(MovementInputEvent event) {
        if (module.sneak.getValue() && !event.getInput().sneak) {
            if (module.timer.passed(module.sneakDelay.getValue())) {
                sneak = !sneak;
                module.timer.reset();
            }

            event.getInput().sneak = sneak;
        }
    }

}
