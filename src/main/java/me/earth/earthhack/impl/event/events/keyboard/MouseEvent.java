package me.earth.earthhack.impl.event.events.keyboard;

import me.earth.earthhack.api.event.events.Event;

/**
 * While this extends {@link Event} cancellation is only meant as a means to
 * communicate to other modules that the event has already been processed.
 */
public class MouseEvent extends Event {
    private final int button;
    private final boolean state;

    public MouseEvent(int button, boolean state) {
        this.button = button;
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public int getButton() {
        return button;
    }

}
